package com.pigdroid.spring.social.repository;

import java.util.Set;

import org.springframework.data.repository.CrudRepository;

import com.pigdroid.spring.social.domain.Role;

public interface RoleRepository extends CrudRepository<Role, Long> {

    @Override
	Set<Role> findAll();

    Role findByName(String name);

}
