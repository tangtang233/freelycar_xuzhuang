package com.geariot.platform.freelycar.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.geariot.platform.freelycar.entities.Permission;
import com.geariot.platform.freelycar.entities.Role;


public class PermissionsList {
	private static final Logger log = LogManager.getLogger();
	
	private static final String FILENAME = "permissions.txt";
	
	private static final String ROLE_START = "[Roles]";
	
	private static final String DES_SPLITER = ",";
	private static final String PER_SPLITER = ";";
	
	public static Set<Role> getRoles(){
		Set<Role> roles = new HashSet<>();
		BufferedReader br = null;
		String filePath = PermissionsList.class.getClassLoader().getResource(FILENAME).getPath();
		File file = new File(filePath);
		try {
			if(file.isFile() && file.exists()){
	        	br = new BufferedReader(new FileReader(file));
	        }
			String line = null;
			while((line = br.readLine()) != null){
				if(line.trim().equals(ROLE_START)){
					while((line = br.readLine()) != null){
						String[] role_per = line.split("=");
						String[] role = role_per[0].split(DES_SPLITER);
						Role temp = new Role();
						temp.setId(Integer.parseInt(role[0]));
						temp.setRoleName(role[1]);
						temp.setDescription(role[2]);
						Set<Permission> tempPer = new HashSet<>();
						for(String per : role_per[1].split(PER_SPLITER)){
							Permission perTemp = new Permission();
							perTemp.setPermission(per);
							tempPer.add(perTemp);
						}
						temp.setPermissions(tempPer);
						roles.add(temp);
					}
				}
			}
		} catch (FileNotFoundException e) {
			log.debug("文件:" + filePath + "　未找到");
			e.printStackTrace();
		} catch (IOException e) {
			log.debug("文件读取出错");
			e.printStackTrace();
		} finally {
			if(br != null){
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return roles;
	}
	
}
