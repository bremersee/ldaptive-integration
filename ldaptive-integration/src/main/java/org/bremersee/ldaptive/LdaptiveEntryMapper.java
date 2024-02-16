/*
 * Copyright 2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.bremersee.ldaptive;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.ldaptive.AttributeModification;
import org.ldaptive.AttributeModification.Type;
import org.ldaptive.LdapAttribute;
import org.ldaptive.LdapEntry;
import org.ldaptive.ModifyRequest;
import org.ldaptive.beans.LdapEntryMapper;
import org.ldaptive.dn.Dn;
import org.ldaptive.dn.NameValue;
import org.ldaptive.dn.RDn;
import org.ldaptive.transcode.ValueTranscoder;

/**
 * The ldap entry mapper.
 *
 * @param <T> the type of the domain object
 * @author Christian Bremer
 */
public interface LdaptiveEntryMapper<T> extends LdapEntryMapper<T> {

  /**
   * Get object classes of the ldap entry. The object classes are only required, if a new ldap entry
   * should be persisted.
   *
   * @return the object classes of the ldap entry
   */
  String[] getObjectClasses();

  @Override
  String mapDn(T domainObject);

  /**
   * Map a ldap entry into a domain object.
   *
   * @param ldapEntry the ldap entry
   * @return the domain object
   */
  T map(LdapEntry ldapEntry);

  @Override
  void map(LdapEntry source, T destination);

  @Override
  default void map(T source, LdapEntry destination) {
    mapAndComputeModifications(source, destination);
  }

  /**
   * Map and compute attribute modifications (see
   * {@link LdapEntry#computeModifications(LdapEntry, LdapEntry)}**).
   *
   * @param source the source (domain object); required
   * @param destination the destination (ldap entry); required
   * @return the attribute modifications
   */
  AttributeModification[] mapAndComputeModifications(
      T source,
      LdapEntry destination);

  /**
   * Map and compute modify request.
   *
   * @param source the source (domain object); required
   * @param destination the destination (ldap entry); required
   * @return the modify request
   */
  default ModifyRequest mapAndComputeModifyRequest(
      T source,
      LdapEntry destination) {
    return new ModifyRequest(destination.getDn(), mapAndComputeModifications(source, destination));
  }

  /**
   * Gets attribute value.
   *
   * @param <T> the type parameter
   * @param ldapEntry the ldap entry; required
   * @param name the name; required
   * @param valueTranscoder the value transcoder; required
   * @param defaultValue the default value
   * @return the attribute value
   */
  static <T> T getAttributeValue(
      LdapEntry ldapEntry,
      String name,
      ValueTranscoder<T> valueTranscoder,
      T defaultValue) {
    LdapAttribute attr = ldapEntry == null ? null : ldapEntry.getAttribute(name);
    T value = attr != null ? attr.getValue(valueTranscoder.decoder()) : null;
    return value != null ? value : defaultValue;
  }

  /**
   * Gets attribute values.
   *
   * @param <T> the type parameter
   * @param ldapEntry the ldap entry; required
   * @param name the name; required
   * @param valueTranscoder the value transcoder; required
   * @return the attribute values
   */
  static <T> Collection<T> getAttributeValues(
      LdapEntry ldapEntry,
      String name,
      ValueTranscoder<T> valueTranscoder) {
    LdapAttribute attr = ldapEntry == null ? null : ldapEntry.getAttribute(name);
    Collection<T> values = attr != null ? attr.getValues(valueTranscoder.decoder()) : null;
    return values != null ? values : new ArrayList<>();
  }

  /**
   * Gets attribute values as set.
   *
   * @param <T> the type parameter
   * @param ldapEntry the ldap entry; required
   * @param name the name; required
   * @param valueTranscoder the value transcoder; required
   * @return the attribute values as set
   */
  static <T> Set<T> getAttributeValuesAsSet(
      LdapEntry ldapEntry,
      String name,
      ValueTranscoder<T> valueTranscoder) {
    return new LinkedHashSet<>(getAttributeValues(ldapEntry, name, valueTranscoder));
  }

  /**
   * Gets attribute values as list.
   *
   * @param <T> the type parameter
   * @param ldapEntry the ldap entry; required
   * @param name the name; required
   * @param valueTranscoder the value transcoder; required
   * @return the attribute values as list
   */
  static <T> List<T> getAttributeValuesAsList(
      LdapEntry ldapEntry,
      String name,
      ValueTranscoder<T> valueTranscoder) {
    return new ArrayList<>(getAttributeValues(ldapEntry, name, valueTranscoder));
  }

  /**
   * Replaces the value of the attribute with the specified value.
   *
   * @param <T> the type of the domain object
   * @param ldapEntry the ldap entry; required
   * @param name the attribute name; required
   * @param value the attribute value; can be null
   * @param isBinary specifies whether the attribute value is binary or not
   * @param valueTranscoder the value transcoder (can be null if value is also null)
   * @param modifications the list of modifications; required
   */
  static <T> void setAttribute(
      LdapEntry ldapEntry,
      String name,
      T value,
      boolean isBinary,
      ValueTranscoder<T> valueTranscoder,
      List<AttributeModification> modifications) {

    setAttributes(
        ldapEntry,
        name,
        value != null ? Collections.singleton(value) : null,
        isBinary,
        valueTranscoder,
        modifications);
  }

  /**
   * Replaces the values of the attribute with the specified values.
   *
   * @param <T> the type of the domain object
   * @param ldapEntry the ldap entry; required
   * @param name the attribute name; required
   * @param values the values of the attribute
   * @param isBinary specifies whether the attribute value is binary or not
   * @param valueTranscoder the value transcoder (can be null if values is also null)
   * @param modifications the list of modifications; required
   */
  static <T> void setAttributes(
      LdapEntry ldapEntry,
      String name,
      Collection<T> values,
      boolean isBinary,
      ValueTranscoder<T> valueTranscoder,
      List<AttributeModification> modifications) {

    Collection<T> realValues = Stream.ofNullable(values)
        .flatMap(Collection::stream)
        .filter(value -> {
          if (value instanceof CharSequence cs) {
            return !cs.isEmpty();
          }
          return value != null;
        })
        .collect(Collectors.toList());
    LdapAttribute attr = ldapEntry.getAttribute(name);
    if (attr == null && !realValues.isEmpty()) {
      addAttributes(ldapEntry, name, realValues, isBinary, valueTranscoder, modifications);
    } else if (attr != null) {
      if (realValues.isEmpty()) {
        ldapEntry.removeAttribute(name);
        modifications.add(
            new AttributeModification(
                Type.DELETE,
                attr));
      } else if (!new ArrayList<>(realValues)
          .equals(new ArrayList<>(attr.getValues(valueTranscoder.decoder())))) {
        LdapAttribute newAttr = new LdapAttribute();
        newAttr.setBinary(isBinary);
        newAttr.setName(name);
        newAttr.addValues(valueTranscoder.encoder(), realValues);
        ldapEntry.addAttributes(newAttr);
        modifications.add(
            new AttributeModification(
                Type.REPLACE,
                newAttr));
      }
    }
  }

  /**
   * Adds the specified value to the attribute with the specified name.
   *
   * @param <T> the type of the domain object
   * @param ldapEntry the ldap entry; required
   * @param name the attribute name; required
   * @param value the attribute value; can be null
   * @param isBinary specifies whether the attribute value is binary or not
   * @param valueTranscoder the value transcoder; required
   * @param modifications the list of modifications; required
   */
  static <T> void addAttribute(
      LdapEntry ldapEntry,
      String name,
      T value,
      boolean isBinary,
      ValueTranscoder<T> valueTranscoder,
      List<AttributeModification> modifications) {
    addAttributes(
        ldapEntry,
        name,
        value != null ? Collections.singleton(value) : null,
        isBinary,
        valueTranscoder,
        modifications);
  }

  /**
   * Adds the specified values to the attribute with the specified name.
   *
   * @param <T> the type of the domain object
   * @param ldapEntry the ldap entry; required
   * @param name the attribute name; required
   * @param values the attribute values; can be null
   * @param isBinary specifies whether the attribute value is binary or not
   * @param valueTranscoder the value transcoder; required
   * @param modifications the list of modifications; required
   */
  static <T> void addAttributes(
      LdapEntry ldapEntry,
      String name,
      Collection<T> values,
      boolean isBinary,
      ValueTranscoder<T> valueTranscoder,
      List<AttributeModification> modifications) {
    Collection<T> realValues = Stream.ofNullable(values)
        .flatMap(Collection::stream)
        .filter(value -> {
          if (value instanceof CharSequence cs) {
            return !cs.isEmpty();
          }
          return value != null;
        })
        .collect(Collectors.toList());
    if (realValues.isEmpty()) {
      return;
    }
    LdapAttribute attr = ldapEntry.getAttribute(name);
    if (attr == null) {
      LdapAttribute newAttr = new LdapAttribute();
      newAttr.setBinary(isBinary);
      newAttr.setName(name);
      newAttr.addValues(valueTranscoder.encoder(), realValues);
      ldapEntry.addAttributes(newAttr);
      modifications.add(
          new AttributeModification(
              Type.ADD,
              newAttr));
    } else {
      List<T> newValues = new ArrayList<>(
          getAttributeValues(ldapEntry, name, valueTranscoder));
      newValues.addAll(realValues);
      setAttributes(ldapEntry, name, newValues, attr.isBinary(), valueTranscoder, modifications);
    }
  }

  /**
   * Removes an attribute with the specified name.
   *
   * @param ldapEntry the ldap entry; required
   * @param name the name; required
   * @param modifications the modifications; required
   */
  static void removeAttribute(
      LdapEntry ldapEntry,
      String name,
      List<AttributeModification> modifications) {
    LdapAttribute attr = ldapEntry.getAttribute(name);
    if (attr == null) {
      return;
    }
    ldapEntry.removeAttributes(attr);
    modifications.add(
        new AttributeModification(
            Type.DELETE,
            attr));
  }

  /**
   * Removes an attribute with the specified value. If the value is {@code null}, the whole
   * attribute will be removed.
   *
   * @param <T> the type of the domain object
   * @param ldapEntry the ldap entry; required
   * @param name the name; required
   * @param value the value; can be null
   * @param valueTranscoder the value transcoder; required
   * @param modifications the modifications; required
   */
  static <T> void removeAttribute(
      LdapEntry ldapEntry,
      String name,
      T value,
      ValueTranscoder<T> valueTranscoder,
      List<AttributeModification> modifications) {
    LdapAttribute attr = ldapEntry.getAttribute(name);
    if (attr == null) {
      return;
    }
    if (value == null) {
      removeAttribute(ldapEntry, name, modifications);
    } else {
      removeAttributes(ldapEntry, name, Collections.singleton(value), valueTranscoder,
          modifications);
    }
  }

  /**
   * Remove attributes with the specified values. If values are empty or {@code null}, no attributes
   * will be removed.
   *
   * @param <T> the type of the domain object
   * @param ldapEntry the ldap entry; required
   * @param name the name; required
   * @param values the values
   * @param valueTranscoder the value transcoder; required
   * @param modifications the modifications; required
   */
  static <T> void removeAttributes(
      LdapEntry ldapEntry,
      String name,
      Collection<T> values,
      ValueTranscoder<T> valueTranscoder,
      List<AttributeModification> modifications) {

    LdapAttribute attr = ldapEntry.getAttribute(name);
    if (attr == null || values == null || values.isEmpty()) {
      return;
    }
    List<T> newValues = new ArrayList<>(getAttributeValues(ldapEntry, name, valueTranscoder));
    newValues.removeAll(values);
    setAttributes(ldapEntry, name, newValues, attr.isBinary(), valueTranscoder, modifications);
  }

  /**
   * Create dn string.
   *
   * @param rdn the rdn; required
   * @param rdnValue the rdn value; required
   * @param baseDn the base dn; required
   * @return the string
   */
  static String createDn(
      String rdn,
      String rdnValue,
      String baseDn) {
    return Dn.builder()
        .add(new RDn(new NameValue(rdn, rdnValue)))
        .add(new Dn(baseDn))
        .build()
        .format();
  }

  /**
   * Gets rdn.
   *
   * @param dn the dn
   * @return the rdn
   */
  static String getRdn(String dn) {
    if (dn == null) {
      return null;
    }
    Dn parsedDn = new Dn(dn);
    if (parsedDn.isEmpty()) {
      return null;
    }
    return parsedDn.getRDn().getNameValue().getStringValue();
  }

}
