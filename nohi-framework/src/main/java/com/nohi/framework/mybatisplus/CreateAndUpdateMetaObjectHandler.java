package com.nohi.framework.mybatisplus;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.nohi.common.utils.SecurityUtils;
import org.apache.ibatis.reflection.MetaObject;

import java.time.LocalDateTime;

/**
 * MP注入处理器
 * <p>created on 2022/1/28 17:40</p>
 * @author dute7liang
 */
public class CreateAndUpdateMetaObjectHandler implements MetaObjectHandler {

	@Override
	public void insertFill(MetaObject metaObject) {
		//根据属性名字设置要填充的值
		if (metaObject.hasGetter("createTime")) {
			if (metaObject.getValue("createTime") == null) {
				this.setFieldValByName("createTime", LocalDateTime.now(), metaObject);
			}
		}
		if (metaObject.hasGetter("createBy")) {
			if (metaObject.getValue("createBy") == null) {
				this.setFieldValByName("createBy", SecurityUtils.getUsername(), metaObject);
			}
		}
	}

	@Override
	public void updateFill(MetaObject metaObject) {
		if (metaObject.hasGetter("updateBy")) {
			if (metaObject.getValue("updateBy") == null) {
				this.setFieldValByName("updateBy", SecurityUtils.getUsername(), metaObject);
			}
		}
		if (metaObject.hasGetter("updateTime")) {
			if (metaObject.getValue("updateTime") == null) {
				this.setFieldValByName("updateTime", LocalDateTime.now(), metaObject);
			}
		}
	}

}
