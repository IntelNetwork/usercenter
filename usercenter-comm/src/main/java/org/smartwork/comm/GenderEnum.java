package org.smartwork.comm;

import org.forbes.comm.utils.ConvertUtils;
import org.forbes.comm.vo.ResultEnum;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/***
 */
public enum GenderEnum {

    MAN(0,"男"),
    WOMAN(1,"女");

    private Integer code;
    private String name;


    GenderEnum(Integer code,String name){
        this.code = code;
        this.name = name;
    }


    /***
     *
     * @return
     */
    public static List<ResultEnum> resultEnums(){
        return Arrays.asList(GenderEnum.values())
                .stream().map(genderEnum -> ResultEnum.ResultEnumBuild
                        .build()
                        .setCode(genderEnum.getCode())
                        .setName(genderEnum.getName())).collect(Collectors.toList());
    }

    /***
     *   判断是否存在
     * @param code
     * @return
     */
    public static boolean existsCode(Integer code){
        return Arrays.asList(GenderEnum.values()).stream()
                .filter(genderEnum -> ConvertUtils.isNotEmpty(code)&&genderEnum.getCode().equals(code))
                .count() >=  1;
    }

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
