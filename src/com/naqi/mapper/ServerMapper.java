package com.naqi.mapper;

import java.util.List;

import com.naqi.model.Server;

public interface ServerMapper {
	/**
	 * 增加服务
	 * @param server
	 */
	public void save(Server server);
	/**
	 * 更新服务
	 * @param server
	 * @return
	 */
	public boolean update(Server server);
	/**
	 * 删除服务
	 * @param id
	 * @return
	 */
	public boolean delete(int id);
	/**
	 * 根据id查找服务
	 * @param id
	 * @return
	 */
	public Server findById(int id);
	/**
	 * 根据type查找服务
	 * @param id
	 * @return
	 */
	public List<Server> findByType(int type);
	/**
	 * 查找全部用户
	 * @return
	 */
	public List<Server> findAll();
}
