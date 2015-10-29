package org.opensrp.web.controller;

import static ch.lambdaj.collection.LambdaCollections.with;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import org.json.JSONException;
import org.opensrp.connector.openmrs.service.OpenmrsUserService;
import org.opensrp.dto.AclDTO;
import org.opensrp.dto.RoleDTO;
import org.opensrp.dto.form.FormSubmissionDTO;
import org.opensrp.form.domain.FormSubmission;
import org.opensrp.form.service.FormSubmissionConverter;
import org.opensrp.register.mcare.domain.Acl;
import org.opensrp.register.mcare.service.AclService;
import org.opensrp.register.mcare.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import ch.lambdaj.collection.LambdaList;
import ch.lambdaj.function.convert.Converter;

import com.google.gson.Gson;

@Controller
public class AclController {

	private RoleService roleService;
	private AclService aclService;
	private OpenmrsUserService openmrsUserService;

	@Autowired
	public AclController(RoleService roleService, AclService aclService,
			OpenmrsUserService openmrsUserService) {
		this.roleService = roleService;
		this.aclService = aclService;
		this.openmrsUserService = openmrsUserService;
	}

	@RequestMapping(headers = { "Accept=application/json" }, method = POST, value = "/add-role")
	public ResponseEntity<HttpStatus> addRole(@RequestBody RoleDTO roleDTO) {
		roleService.addRole(roleDTO);
		return new ResponseEntity<>(CREATED);
	}

	@RequestMapping(headers = { "Accept=application/json" }, method = POST, value = "/add-acl")
	public ResponseEntity<HttpStatus> addAcl(@RequestBody AclDTO aclDTO) {
		aclService.addAcl(aclDTO);
		return new ResponseEntity<>(CREATED);
	}

	@RequestMapping(method = GET, value = "/all-user-name")
	@ResponseBody
	public ResponseEntity<String> getAllUserName() throws JSONException {
		return new ResponseEntity<>(new Gson().toJson(openmrsUserService
				.getAllUsers()), OK);
	}

	@RequestMapping(method = GET, value = "/role-access-tokens")
	@ResponseBody
	public AclDTO getRoleAndAccessTokens(@RequestParam String userName) {
		return aclService.getRoleAndAccessTokens(userName);
	}
}
