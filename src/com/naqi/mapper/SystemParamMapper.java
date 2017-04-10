package com.naqi.mapper;

import java.util.List;

import com.naqi.model.SystemParam;

public interface SystemParamMapper {
	/**
	 * 增加系统参数
	 * @param system_param
	 */
	public void save(SystemParam system_param);
	/**
	 * 更新系统参数
	 * @param system_param
	 * @return
	 */
	public boolean update(SystemParam system_param);
	/**
	 * 删除系统参数
	 * @param id
	 * @return
	 */
	public boolean delete(int id);
	/**
	 * 查找全部系统参数
	 * @return
	 */
	public List<SystemParam> findAll();
}
