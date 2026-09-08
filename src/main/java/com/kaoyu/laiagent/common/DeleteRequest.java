package com.kaoyu.laiagent.common;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 吾遇小郭
 */
@Data
public class DeleteRequest implements Serializable {

    /**
     * 删除数据用的唯一id
     */
    private Long id;

    private static final long serialVersionUID = 1L;

}
