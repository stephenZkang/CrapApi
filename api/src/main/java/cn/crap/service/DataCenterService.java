package cn.crap.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.crap.dto.PickDto;
import cn.crap.framework.base.BaseService;
import cn.crap.framework.base.IBaseDao;
import cn.crap.inter.dao.ICacheDao;
import cn.crap.inter.service.IDataCenterService;
import cn.crap.model.DataCenter;
import cn.crap.utils.Const;
import cn.crap.utils.GetBeanBySetting;
import cn.crap.utils.MyString;
import cn.crap.utils.Page;
import cn.crap.utils.Tools;

@Service
public class DataCenterService extends BaseService<DataCenter>
		implements IDataCenterService {
	private ICacheDao cacheDao = GetBeanBySetting.getCacheDao();

	@Resource(name="dataCenterDao")
	public void setDao(IBaseDao<DataCenter> dao ) {
		super.setDao(dao, new DataCenter());
	}
	
	@Override
	@Transactional
	public void getDataCenterPick(List<PickDto> picks,Map<String,Object> map, String idPre,String parentId,String type, String deep,String value,String suffix){
		if(MyString.isEmpty(type)){
			type = "MODULE";
		}
		PickDto pick = null;
		
		if(map == null){
			map = Tools.getMap("parentId",parentId,"type", type);
		}else{
			map.putAll(Tools.getMap("parentId",parentId,"type", type));
		}
			
		for (DataCenter m : findByMap(map, null,null)) {
			if(MyString.isEmpty(value))
				pick = new PickDto(idPre+m.getId(), deep+m.getName());
			else
				pick = new PickDto(idPre+m.getId(), value.replace("projectId", m.getProjectId()).replace("moduleId", m.getId()).replace("moduleName", m.getName()),deep+m.getName()+suffix);
			picks.add(pick);
			getDataCenterPick(picks, null, idPre,m.getId(), type, deep+Const.LEVEL_PRE , value,suffix);
		}
	}

	@Override
	@Transactional
	public List<String> getList(Byte status, String type, String userId) {
		List<Byte> statuss = null;
		if(status != null){
			statuss= new ArrayList<Byte>();
			statuss.add(status);
		}
		return getListByStatuss(statuss, type, userId);
	}
	
	@Override
	@Transactional
	public List<String> getListByStatuss(List<Byte> statuss, String type, String userId) {
		Page page = new Page();
		page.setSize(2000);// 最多显示钱2000条
		List<String> ids = new ArrayList<String>();
		List<DataCenter> dcs = findByMap(Tools.getMap("status|in", statuss, "type", type, "userId", userId), page, null);
		for(DataCenter dc:dcs){
			ids.add(dc.getId());
		}
		return ids;
	}
	
}
