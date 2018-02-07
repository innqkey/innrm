package com.huisou.config;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.common.JdbcSystemUtils;
import com.huisou.mapper.CustomerPoMapper;
import com.huisou.mapper.ItemsPoMapper;
import com.huisou.mapper.SalesManPoMapper;
import com.huisou.po.CustomerPo;
import com.huisou.po.ItemsPo;
import com.huisou.po.SalesManPo;
import com.huisou.po.UniversalPo;
import com.huisou.vo.HscomApplicationVo;

@Component
public class SycItemCustScheduled {

	//直接调用dao层，方便操作数据
	@Autowired
	private ItemsPoMapper itemsPoMapper;
	
	@Autowired
	private CustomerPoMapper customerPoMapper;
	
	@Autowired
	private SalesManPoMapper saleMapper;
	
	private  static  final Logger logger = LoggerFactory.getLogger(SycItemCustScheduled.class);
	
	/*
	 * 第一次同步数据，只执行一次
	 */
//	@Scheduled(cron="0 00 19 * * ?") 
//	@Scheduled(cron="0 19 18 14 10 ?")
	@Scheduled(cron="0 22 11 * * ?")
	public void executeFirstSysInfo(){
		logger.info("begin同步客户=================");
//		int num = customerPoMapper.countRelatecustid();
		int num = 0;
		//第一次，同步数据
		if(num==0){
			List<CustomerPo> custPos = customerPoMapper.selectAll();
			if(null!=custPos&&custPos.size()>0){
				for(CustomerPo custPo : custPos){
					try {
						if(custPo.getCustid()>3961&&custPo.getRelatecustid()==0){
							//根据联系人姓名和联系电话查询system系统id
							Integer relaCustId = JdbcSystemUtils.getCustIdByNameAndCompany(custPo.getContact(),custPo.getCompanyname());
							logger.info("根据联系人姓名和联系电话查询system系统id=="+relaCustId);
							if(null!=relaCustId&&relaCustId>0){
								CustomerPo updateCustPo = new CustomerPo();
								updateCustPo.setCustid(custPo.getCustid());
								updateCustPo.setRelatecustid(relaCustId);
								customerPoMapper.updateByPrimaryKeySelective(updateCustPo);
							}
						}
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			
//			List<ItemsPo> itemsPos = itemsPoMapper.selectAll();
//			if(null!=itemsPos&&itemsPos.size()>0){
//				for(ItemsPo itemPo : itemsPos){
//					try {
//						//根据项目名称和项目类型查询system系统id
//						HscomApplicationVo appVo = JdbcSystemUtils.getAppVoByParas(itemPo.getItemname(), itemPo.getItemtype());
//						if(null!=appVo){
//							ItemsPo updateItemPo = new ItemsPo();
//							updateItemPo.setItemid(itemPo.getItemid());
//							updateItemPo.setRelateitemid(appVo.getId());
//							itemsPoMapper.updateByPrimaryKeySelective(updateItemPo);
//						}
//					} catch (SQLException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				}
//			}
		}else{
			logger.info("已经同步客户与项目，只执行一次，不在执行=================");
		}
		logger.info("end同步客户=================");
	}
	
	
	/*
	 * 同步合同进度数据，每30分钟执行一次
	 */
	@Scheduled(cron="0 */30 * * * ?")
	public void executeItemInfo(){
		logger.info("begin同步合同进度数据=================");
		List<ItemsPo> itemsPos = itemsPoMapper.selectAll();
		if(null!=itemsPos&&itemsPos.size()>0){
			for(ItemsPo itemPo : itemsPos){
				try {
					//根据项目名称和项目类型查询system系统id
					if(null!=itemPo.getRelateitemid()&&itemPo.getRelateitemid()>0){
						HscomApplicationVo appVo = JdbcSystemUtils.getAppVoByParas(itemPo.getRelateitemid());
						UniversalPo appconfig=JdbcSystemUtils.getAppConfigByParas(appVo.getId());
						if(null!=appVo){
							//standby1-简称,standby2-域名,relatecontstatus-关联合同状态,relatedesc-关联desc,制作描述
							ItemsPo updateItemPo = new ItemsPo();
							updateItemPo.setItemid(itemPo.getItemid());
							updateItemPo.setStandby1(appVo.getShortname());
							if(appconfig!=null){
								updateItemPo.setStandby2((String)appconfig.get("code"));
							}
							updateItemPo.setRelatecontstatus(appVo.getStatus());
							updateItemPo.setRelatedesc(appVo.getDesc());
							updateItemPo.setRelateitemid(appVo.getId());
							itemsPoMapper.updateByPrimaryKeySelective(updateItemPo);
							logger.info("同步合同进度数据itemid=="+itemPo.getItemid());
						}
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		logger.info("end同步合同进度数据=================");
	}
	
	/*
	 * 同步合同进度数据，每30分钟执行一次
	 */
	@Scheduled(cron="0 12 11 * * ?")
	public void executeSystemToCrmItemInfo(){
		logger.info("begin同步system项目到crm系统中=================");
		List<ItemsPo> itemsPos = JdbcSystemUtils.sysInputCrm();
		if(null!=itemsPos&&itemsPos.size()>0){
			for(ItemsPo itemPo : itemsPos){
				try {
					//根据项目名称和项目类型查询system系统id
					ItemsPo relaPo = new ItemsPo();
					relaPo.setRelateitemid(itemPo.getRelateitemid());
					List<ItemsPo> relaList = itemsPoMapper.select(relaPo);
					if(relaList!=null&&relaList.size()>0){
						continue;
					}else{
						ItemsPo itemNamePo = new ItemsPo();
						itemNamePo.setItemname(itemPo.getItemname());
						itemNamePo.setItemtype(itemPo.getItemtype());
						List<ItemsPo> itemNameList = itemsPoMapper.select(itemNamePo);
						if(itemNameList!=null&&itemNameList.size()>0){
							continue;
						}else{
							logger.info("crm不存在项目===system项目id="+itemPo.getRelateitemid()+";项目名称="+itemPo.getItemname());
							insertItemPo(itemPo);
						}
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		logger.info("end同步system项目到crm系统中=================");
	}
	
	private void insertItemPo(ItemsPo itemPo){
		CustomerPo custPo = JdbcSystemUtils.getCustPo(itemPo.getCustid(), itemPo.getCreatedate());
		Map<String,Integer> map = new HashMap<String,Integer>();
		int saleId = 0;
		int custId = 0;
		if(null!=custPo){
			
			CustomerPo reqPo1 = new CustomerPo();
			reqPo1.setRelatecustid(custPo.getRelatecustid());
			List<CustomerPo> resList1 = customerPoMapper.select(reqPo1);
			if(null!=resList1&&resList1.size()>0){
				custId = resList1.get(0).getCustid();
				saleId = resList1.get(0).getSaleid();
			}else{
				CustomerPo reqPo2 = new CustomerPo();
				reqPo2.setContact(custPo.getContact());
				reqPo2.setCompanyname(custPo.getCompanyname());
				List<CustomerPo> resList2 = customerPoMapper.select(reqPo2);
				if(null!=resList2&&resList2.size()>0){
					custId = resList2.get(0).getCustid();
					saleId = resList2.get(0).getSaleid();
				}else{
					logger.info("业务员查询======客户名称="+custPo.getContact()+";公司名称="+custPo.getCompanyname()+";system客户id="+custPo.getRelatecustid());
					saleId = getsaleid(JdbcSystemUtils.getsalename(custPo.getSaleid()));
					custPo.setSaleid(saleId);
					customerPoMapper.insertCustomerReturnId(custPo);
					custId = custPo.getCustid();
				}
			}
			itemPo.setSaleid(saleId);
			itemPo.setCustid(custId);
			itemsPoMapper.insertSelective(itemPo);
		}else{
			logger.info("system项目不存在==="+itemPo.getItemname());
		}
	}
	
	public Integer getsaleid(String salename){
		if(StringUtils.isBlank(salename)){
			logger.info("业务员不存在======");
			return 0;
		}
		SalesManPo po = new SalesManPo();
		po.setSalename(salename);
		List<SalesManPo> saleList = saleMapper.select(po);
		if(null!=saleList&&saleList.size()>0){
			return saleList.get(0).getSaleid();
		}else{
			logger.info("业务员不存在======"+salename);
		}
		return 0;
	}
}
