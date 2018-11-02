package cn.wizzer.app.sys.modules.services;

import cn.wizzer.app.sys.modules.models.Sys_dict;
import cn.wizzer.framework.base.service.BaseService;
import org.nutz.mvc.upload.TempFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by 王怀先 on 2017/1/19.
 */
public interface SysDictService extends BaseService<Sys_dict> {
    String getNameByCode(String code);
    String getNameById(String id);
    String getIdByName(String name);
    String getIdByNameAndCode(String name,String code);
    String getIdByCode(String code);
    List<Sys_dict> getSubListByPath(String path);
    List<Sys_dict> getSubListById(String id);
    List<Sys_dict> getSubListByCode(String code);
    Map getSubMapByPath(String path);
    Map getSubMapById(String id);
    Map getSubMapByCode(String code);
    void save(Sys_dict dict, String pid);
    void deleteAndChild(Sys_dict dict);


    /**
     * 得到航线运输的ID
     * @return
     */
    public String getDictOtypeId();

    List<Map<String,Object>> bindDDLbyM(String path,Integer pagenumber, Integer pagesize);

    /**
     * 上传工具图片
     */
    void uploadTools(TempFile tf, String id) throws IOException;

    /**
     * 根据工具parentCode得到子元素列表
     * @param parentCode
     * @return
     */
//    List<Map<String,Object>> getDictToolList(String parentCode, String airportId);

    /**
     * 得到子元素列表
     */
    List<Map<String,Object>> getKidToolList(String airportId, String parentId);

    List<Map<String,Object>> getDictToolList(String airportId, String parentId);
}
