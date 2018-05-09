package com.code.platform.task.util.validation;

import javax.validation.ConstraintViolation;
import java.util.*;

public class EntityValidata {

    /**
     * 新增验证实体Bean
     *
     * @param object
     *
     * @throws Exception
     */
    public void onInsertValidata(Object object) throws Exception {
        if (object == null) {
            throw new Exception("对象不能为空!");
        }
        Set<ConstraintViolation<Object>> set = new HashSet<ConstraintViolation<Object>>();
        set = ValidatorFactory.getInstance().validate(object, FieldValidata.class);

        if (set.size() > 0) {
            List<String> errorList = new ArrayList<String>();
            String errorText = "";
            for (Iterator<ConstraintViolation<Object>> iter = set.iterator(); iter.hasNext(); ) {
                ConstraintViolation<Object> bean = iter.next();
                errorList.add(bean.getMessage());
                errorText += bean.getMessage() + "<br/>";
            }

            throw new Exception(errorText);
        }
    }

    /**
     * 修改验证实体Bean
     *
     * @param object
     *
     * @throws Exception
     */
    public void onUpdateValidata(Object object) throws Exception {
        if (object == null) {
            throw new Exception("对象不能为空!");
        }
        Set<ConstraintViolation<Object>> set = new HashSet<ConstraintViolation<Object>>();
        set = ValidatorFactory.getInstance().validate(object, AllValidata.class);

        if (set.size() > 0) {
            List<String> errorList = new ArrayList<String>();
            String errorText = "";
            for (Iterator<ConstraintViolation<Object>> iter = set.iterator(); iter.hasNext(); ) {
                ConstraintViolation<Object> bean = iter.next();
                errorList.add(bean.getMessage());
                errorText += bean.getMessage() + "<br/>";
            }
            throw new Exception(errorText);
        }
    }

    /**
     * 删除对象时验证实体Bean
     * 根据主键进行验证
     *
     * @param object
     *
     * @throws Exception
     */
    public void onRemoveValidata(Object object) throws Exception {
        if (object == null) {
            throw new Exception("对象不能为空!");
        }
        Set<ConstraintViolation<Object>> set = new HashSet<ConstraintViolation<Object>>();
        set = ValidatorFactory.getInstance().validate(object, IdValidata.class);

        if (set.size() > 0) {
            List<String> errorList = new ArrayList<String>();
            String errorText = "";
            for (Iterator<ConstraintViolation<Object>> iter = set.iterator(); iter.hasNext(); ) {
                ConstraintViolation<Object> bean = iter.next();
                errorList.add(bean.getMessage());
                errorText += bean.getMessage() + "<br/>";
            }
            throw new Exception(errorText);
        }
    }

}
