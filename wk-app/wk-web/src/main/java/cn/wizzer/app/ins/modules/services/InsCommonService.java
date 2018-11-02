package cn.wizzer.app.ins.modules.services;

import cn.wizzer.framework.base.service.BaseService;
import cn.wizzer.app.ins.modules.models.Ins_common;

import java.util.List;
import java.util.Map;

public interface InsCommonService extends BaseService<Ins_common>{

    List<Map<String,Object>> getCommonTools(String personid, String commonCode, Integer pagenumber, Integer pagesize);


    void add2Favorite(String personid, String[] baseInsIds, String commonCode);

    void deleteFromMyFavorite(String commonInsId);
}
