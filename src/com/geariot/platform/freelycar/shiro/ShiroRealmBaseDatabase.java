package com.geariot.platform.freelycar.shiro;

import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.shiro.authc.AccountException;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.jdbc.JdbcRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

import com.geariot.platform.freelycar.entities.Admin;
import com.geariot.platform.freelycar.entities.Permission;
import com.geariot.platform.freelycar.entities.Role;
import com.geariot.platform.freelycar.service.AdminService;

public class ShiroRealmBaseDatabase extends JdbcRealm {

	@Autowired
	private AdminService adminService;

	private static final Logger log = LogManager.getLogger(ShiroRealmBaseDatabase.class);
	
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		UsernamePasswordToken upToken = (UsernamePasswordToken) token;
        String username = upToken.getUsername();
        if (username == null) {
            throw new AccountException("Null usernames are not allowed by this realm.");
        }
        SimpleAuthenticationInfo info = null;
        Admin admin = adminService.findAdminByAccount(username);
        if (admin == null) {
        	throw new UnknownAccountException("No account found for user [" + username + "]");
        }
        if(!admin.isCurrent()){
        	final String message = "Account [" + username + "] is not available.";
        	log.debug(message);
        	throw new LockedAccountException(message);
        }
        String password = admin.getPassword();
        info = new SimpleAuthenticationInfo(username, password.toCharArray(), getName());
		return info;
	}

	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		if (principals == null) {
            throw new AuthorizationException("PrincipalCollection method argument cannot be null.");
        }

        String username = (String) getAvailablePrincipal(principals);

        Set<String> roleNames = new HashSet<>();
        Set<String> permissions = new HashSet<>();
        Admin admin = adminService.findAdminByAccount(username);
        if (admin == null) {
        	throw new UnknownAccountException("No account found for user [" + username + "]");
        }
        if(!admin.isCurrent()){
        	final String message = "Account [" + username + "] is not available.";
        	log.debug(message);
        	throw new LockedAccountException(message);
        }
        Role role = admin.getRole();
        roleNames.add(role.getRoleName());
        if(this.permissionsLookupEnabled){
        	for(Permission p : role.getPermissions()){
        		permissions.add(p.getPermission());
        	}
        }
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo(roleNames);
        info.setStringPermissions(permissions);
		return info;
	}
	
}
