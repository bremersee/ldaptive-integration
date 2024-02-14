package org.bremersee.ldaptive.security.authentication.templates;

public class OpenLdapTemplate
    extends UserContainsGroupsTemplate {

  public OpenLdapTemplate() {
    setUserObjectClass("inetOrgPerson");
    setUserUidAttribute("uid");
    setRealNameAttribute("cn");
    setEmailAttribute("mail");
    setMemberAttribute("memberOf");
    setPasswordAttribute(""); // userPassword
  }

}
