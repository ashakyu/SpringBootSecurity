package com.cos.security1.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cos.security1.model.User;

//CRUD 함수를 JpaRepository가 들고 있음.
//@Repository라는 어노테이션이 없어도 Ioc가 가능하다 이유: JpaRepository를 상속 했기 때문에 가능하다. 
public interface UserRepository extends JpaRepository<User, Integer>{
	
}
