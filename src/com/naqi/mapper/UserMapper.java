package com.naqi.mapper;

import com.naqi.model.User;

public interface UserMapper {
	/**
	 * 增加用户
	 * @param user
	 */
	public void save(User user);
	/**
	 * 更新用户
	 * @param user
	 * @return
	 */
	public boolean update(User user);
	/**
	 * 删除用户
	 * @param id
	 * @return
	 */
	public boolean delete(int id);
	/**
	 * 根据id查找用户
	 * @param id
	 * @return
	 */
	public User findById(int id);
	/**
	 * 根据名字获得用户
	 * @param name
	 * @return
	 */
	public User findByAccount(String account);
}
