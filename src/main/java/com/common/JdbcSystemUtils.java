package com.common;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jsqlparser.statement.select.Select;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huisou.po.CustomerPo;
import com.huisou.po.ItemsPo;
import com.huisou.po.SalesManPo;
import com.huisou.po.UniversalPo;
import com.huisou.vo.HscomApplicationVo;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.Statement;

public class JdbcSystemUtils {
	private  static  final Logger logger = LoggerFactory.getLogger(JdbcSystemUtils.class);
	public static Connection getConn() {
	    String driver = FileUtil.getApplicationPro("spring.datasource2.driver-class-name");
	    String url = FileUtil.getApplicationPro("spring.datasource2.url");
	    String username = FileUtil.getApplicationPro("spring.datasource2.username");
	    String password = FileUtil.getApplicationPro("spring.datasource2.password");
	    Connection conn = null;
	    try {
	        Class.forName(driver); //classLoader,加载对应驱动
	        conn = (Connection) DriverManager.getConnection(url, username, password);
	    } catch (ClassNotFoundException e) {
	        e.printStackTrace();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return conn;
	}
	
	
	private static Integer getUserIdByParas(String name,String mobile) throws SQLException {
	    Connection conn = getConn();
	    String sql = "select * from hscom_user where mobile = "+mobile + " and degree = '" + name + "' and status = 1";
	    logger.info("根据名称和手机号码查询业务员getUserIdByParas--sql--"+sql);
	    PreparedStatement pstmt = null;
	    ResultSet rs = null;
	    Integer userId = 0;
	    try {
	        pstmt = (PreparedStatement)conn.prepareStatement(sql);
	        rs = pstmt.executeQuery();
	        while(rs.next()){
		    	userId = rs.getInt("id");
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }finally{
	    	connClose(conn, pstmt, rs);
	    }
	    logger.info("根据名称和手机号码查询业务员getUserIdByParas--userId--"+userId);
	    return userId;
	}

	/*
	 * 根据项目名称和合同号码查询项目
	 */
	public static HscomApplicationVo getAppVoByParas(String name, Integer version) throws SQLException {
	    String sql = "select * from hscom_application where name = '"+name + "' and version = "+version;
	    return getAppVoBySql(sql);
	}
	
	/*
	 * 根据项目id查询项目
	 */
	public static HscomApplicationVo getAppVoByParas(Integer appId) throws SQLException {
	    String sql = "select * from hscom_application where id = "+appId;
	    return getAppVoBySql(sql);
	}
	
	//查询system 中的hscom_application_config中的appid和code
	public static UniversalPo getAppConfigByParas(Integer id) throws Exception {
		String sql="select * from hscom_application_config where app_id= "+id;
		return getAppBysqlUniveral(sql);
	}
	
	
	private static UniversalPo getAppBysqlUniveral(String sql) throws Exception {
	    Connection conn = getConn();
	    logger.info("查询项目getAppConfigByParas--sql--"+sql);
	    PreparedStatement pstmt = null;
	    ResultSet rs = null;
	    Integer appId = 0;
	    UniversalPo vo = null;
	    try {
	        pstmt = (PreparedStatement)conn.prepareStatement(sql);
	        rs = pstmt.executeQuery();
	        while(rs.next()){
	        	vo = new UniversalPo();
	        	appId = rs.getInt("app_id");
	        	vo.put("app_id", appId);
	        	vo.put("code", rs.getString("code"));
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }finally{
	    	connClose(conn, pstmt, rs);
	    }
	    logger.info("查询项目appId---"+appId);
	    return vo;
		
	}


	public static HscomApplicationVo getAppVoBySql(String sql) throws SQLException {
	    Connection conn = getConn();
	    logger.info("查询项目getAppIdByParas--sql--"+sql);
	    PreparedStatement pstmt = null;
	    ResultSet rs = null;
	    Integer appId = 0;
	    HscomApplicationVo vo = null;
	    try {
	        pstmt = (PreparedStatement)conn.prepareStatement(sql);
	        rs = pstmt.executeQuery();
	        while(rs.next()){
	        	vo = new HscomApplicationVo();
	        	appId = rs.getInt("id");
	        	vo.setId(appId);
	        	vo.setDesc(rs.getString("desc"));
	        	vo.setDomain(rs.getString("domain"));
	        	vo.setShortname(rs.getString("shortname"));
	        	vo.setStatus(rs.getInt("status"));
	        	vo.setCust_id(rs.getInt("cust_id"));
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }finally{
	    	connClose(conn, pstmt, rs);
	    }
	    logger.info("查询项目appId---"+appId);
	    return vo;
	}
	
	public static Integer getCustIdByParas(String contact,String mobile) throws SQLException {
	    Connection conn = getConn();
	    String sql = "select id from hscom_customer where linkman = '"+ contact + "' and mobile = " + mobile + " and status = 1";
	    logger.info("根据联系人和手机号查询客户sql=="+sql);
	    PreparedStatement pstmt = null;
	    ResultSet rs = null;
	    Integer id = 0;
	    try {
	        pstmt = (PreparedStatement)conn.prepareStatement(sql);
	        rs = pstmt.executeQuery();
	        while(rs.next()){
	        	id = rs.getInt(1);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }finally{
	    	connClose(conn, pstmt, rs);
	    }
	    logger.info("根据联系人和手机号查询客户getCustIdByParas--客户id-"+id);
	    return id;
	}
	
	public static Integer getCustIdByNameAndCompany(String contact,String mobile) throws SQLException {
	    Connection conn = getConn();
	    String sql = "select id from hscom_customer where linkman = '"+ contact + "' and company_name = '" + mobile + "'";
	    logger.info("根据联系人和手机号查询客户sql=="+sql);
	    PreparedStatement pstmt = null;
	    ResultSet rs = null;
	    Integer id = 0;
	    try {
	        pstmt = (PreparedStatement)conn.prepareStatement(sql);
	        rs = pstmt.executeQuery();
	        while(rs.next()){
	        	id = rs.getInt(1);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }finally{
	    	connClose(conn, pstmt, rs);
	    }
	    logger.info("根据联系人和手机号查询客户getCustIdByParas--客户id-"+id);
	    return id;
	}

	private static void connClose(Connection conn, PreparedStatement pstmt,
			ResultSet rs) throws SQLException {
		if(null!=rs){
			rs.close();
		}
		if(null!=pstmt){
			pstmt.close();
		}
		if(null!=conn){
			conn.close();
		}
	}
	
	public static Map<String,Integer> insertHsCust(CustomerPo customerPo, SalesManPo salePo, ItemsPo itemsPo) {
		
		if(itemsPo.getItemtype()>5){
			return null;
		}
		
		Map<String,Integer> resMap = new HashMap<String,Integer>();
	    Connection conn = getConn();

	    PreparedStatement pstmt = null;
	    
	    ResultSet rs = null; 
	    try {
	    	Integer userId = getUserIdByParas(salePo.getSalename(),salePo.getSalephone());
	    	Integer custId = 0;
	    	Integer appId = 0;
	    	if(null!=customerPo.getRelatecustid()&&customerPo.getRelatecustid()>0){
	    		custId = customerPo.getRelatecustid();
	    	}
//	    	else{
//	    		custId = getCustIdByParas(customerPo.getContact(),customerPo.getPhone());
//	    	}
	    	
	    	if(custId==0){
	    		String sql = "INSERT INTO hscom_customer (company_name, telephone, company_email, company_address, linkman, mobile, summary, user_id) "
	    			    + "VALUES (?,?,?,?,?,?,?,?)";
	    		pstmt = (PreparedStatement) conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
		        pstmt.setString(1, customerPo.getCompanyname());
		        pstmt.setString(2, customerPo.getPhone());
		        pstmt.setString(3, customerPo.getEmail());
		        pstmt.setString(4, customerPo.getAddress());
		        pstmt.setString(5, customerPo.getContact());
		        pstmt.setString(6, customerPo.getPhone());
		        pstmt.setString(7, customerPo.getCompanyintro());
		        pstmt.setInt(8, userId);
		        pstmt.executeUpdate();
		        
		        rs = pstmt.getGeneratedKeys();  
	              
	            if (rs.next()){
	            	custId = rs.getInt(1);
	            }
	            logger.info("插入客户 INSERT INTO hscom_customer==客户id=="+custId+";sql=="+sql);
	    	}else{
	    		String sql = "update hscom_customer set company_name=?,telephone=?,company_email=?,company_address=?,linkman=?,mobile=?,summary=?,user_id=? where id=?";
	    		pstmt = (PreparedStatement) conn.prepareStatement(sql);
	    		pstmt.setString(1, customerPo.getCompanyname());
		        pstmt.setString(2, customerPo.getPhone());
		        pstmt.setString(3, customerPo.getEmail());
		        pstmt.setString(4, customerPo.getAddress());
		        pstmt.setString(5, customerPo.getContact());
		        pstmt.setString(6, customerPo.getPhone());
		        pstmt.setString(7, customerPo.getCompanyintro());
		        pstmt.setInt(8, getUserIdByParas(salePo.getSalename(),salePo.getSalephone()));
		        pstmt.setInt(9, custId);
		        pstmt.executeUpdate();
		        logger.info("更新客户 update hscom_customer==客户id=="+custId+";sql=="+sql);
	    	}
	    	
	    	String appSql = "INSERT INTO hscom_application (name, ios, appreciation, android, requirements, contract, cust_id, user_id, addtime, status, shortname, version, number)" 
	    			+"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	    	pstmt = (PreparedStatement) conn.prepareStatement(appSql,Statement.RETURN_GENERATED_KEYS);
	        pstmt.setString(1, itemsPo.getItemname());
	        pstmt.setString(2, "1");
	        pstmt.setString(3, "0");
	        pstmt.setString(4, "1");
	        pstmt.setString(5, itemsPo.getItemrequire());
	        pstmt.setString(6, itemsPo.getContnum());
	        pstmt.setInt(7, custId);
	        pstmt.setInt(8, userId);
	        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	        pstmt.setString(9, sdf.format(new Date()));
	        pstmt.setString(10, "-1");
	        pstmt.setString(11, itemsPo.getItemname());
	        pstmt.setInt(12, itemsPo.getItemtype());
	        pstmt.setString(13, itemsPo.getStandby3());
	        pstmt.executeUpdate();
	        rs = pstmt.getGeneratedKeys();  
            if (rs.next()){
            	appId = rs.getInt(1);
            }
	        logger.info("插入项目 INSERT hscom_application==项目名称=="+itemsPo.getItemname()+"==appId=="+appId+";==appSql"+appSql);
	        
	        resMap.put("custId", custId);
	        resMap.put("appId", appId);
	        resMap.put("userId", userId);
	        
	        logger.info("返回map==="+resMap.toString());
	        return resMap;

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }finally{
	    	try{
	    		connClose(conn, pstmt, rs);
	    	}catch (SQLException e) {
		        e.printStackTrace();
		    }
	    }
	    
	    return null;
	}
	
	
	public static void updateHsCust(CustomerPo srcCustPo ,CustomerPo customerPo, SalesManPo salePo, ItemsPo srcItemPo, ItemsPo itemsPo) {
		
		if(itemsPo.getItemtype()>5){
			return;
		}
		logger.info("更新客户和项目updateHsCust==");
	    Connection conn = getConn();

	    PreparedStatement pstmt = null;
	    
	    ResultSet rs = null; 
	    try {
	    	Integer userId = 0;
	    	if(null!=salePo){
	    		userId = getUserIdByParas(salePo.getSalename(),salePo.getSalephone());
	    	}
	    	Integer custId = 0;
	    	if(null!=srcCustPo.getRelatecustid()&&srcCustPo.getRelatecustid()>0){
	    		custId = srcCustPo.getRelatecustid();
	    	}
//	    	else{
//	    		custId = getCustIdByParas(customerPo.getContact(),customerPo.getPhone());
//	    	}
	    	Integer appId = 0;
	    	if(custId==0){
	    		String sql = "INSERT INTO hscom_customer (company_name, telephone, company_email, company_address, linkman, mobile, summary, user_id) "
	    			    + "VALUES (?,?,?,?,?,?,?,?)";
	    		pstmt = (PreparedStatement) conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
		        pstmt.setString(1, customerPo.getCompanyname());
		        pstmt.setString(2, customerPo.getPhone());
		        pstmt.setString(3, customerPo.getEmail());
		        pstmt.setString(4, customerPo.getAddress());
		        pstmt.setString(5, customerPo.getContact());
		        pstmt.setString(6, customerPo.getPhone());
		        pstmt.setString(7, customerPo.getCompanyintro());
		        pstmt.setInt(8, userId);
		        pstmt.executeUpdate();
		        
		        rs = pstmt.getGeneratedKeys();  
	              
	            if (rs.next()){
	            	custId = rs.getInt(1);
	            }
	            logger.info("插入hscom_customer==插入客户custId=="+custId+";sql=="+sql);
	    	}else{
	    		String sql = "update hscom_customer set company_name=?,telephone=?,company_email=?,company_address=?,linkman=?,mobile=?,summary=?,user_id=? where id=?";
	    		pstmt = (PreparedStatement) conn.prepareStatement(sql);
	    		pstmt.setString(1, customerPo.getCompanyname());
		        pstmt.setString(2, customerPo.getPhone());
		        pstmt.setString(3, customerPo.getEmail());
		        pstmt.setString(4, customerPo.getAddress());
		        pstmt.setString(5, customerPo.getContact());
		        pstmt.setString(6, customerPo.getPhone());
		        pstmt.setString(7, customerPo.getCompanyintro());
		        pstmt.setInt(8, userId);
		        pstmt.setInt(9, custId);
		        pstmt.executeUpdate();
		        logger.info("更新方法updateHsCust==更新客户sql=="+sql);
	    	}
	    	
	    	//如果项目已经关联
	    	if(null!=srcItemPo.getRelateitemid()&&srcItemPo.getRelateitemid()>0){
	    		appId = srcItemPo.getRelateitemid();
	    	}
//	    	else{
//	    		HscomApplicationVo appVo = getAppVoByParas(srcItemPo.getItemname(),srcItemPo.getItemtype());
//	    		if(null!=appVo){
//	    			appId = appVo.getId();
//	    		}
//	    	}
	    	//如果srcItemPo为空，则表示新建项目
	    	if(appId==0){
	    		String appSql = "INSERT INTO hscom_application (name, version, ios, appreciation, android, requirements, contract, cust_id, user_id, addtime, status, shortname, number)" 
		    			+"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		    	pstmt = (PreparedStatement) conn.prepareStatement(appSql,Statement.RETURN_GENERATED_KEYS);
		        pstmt.setString(1, itemsPo.getItemname());
		        pstmt.setInt(2, itemsPo.getItemtype());
		        pstmt.setString(3, "1");
		        pstmt.setString(4, "0");
		        pstmt.setString(5, "1");
		        pstmt.setString(6, itemsPo.getItemrequire());
		        pstmt.setString(7, itemsPo.getContnum());
		        pstmt.setInt(8, custId);
		        pstmt.setInt(9, userId);
		        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		        pstmt.setString(10, sdf.format(new Date()));
		        pstmt.setString(11, "-1");
		        pstmt.setString(12, itemsPo.getItemname());
		        pstmt.setString(13, itemsPo.getStandby3());
		        pstmt.executeUpdate();
		        rs = pstmt.getGeneratedKeys();  
	              
	            if (rs.next()){
	            	appId = rs.getInt(1);
	            }
		        logger.info("更新updateHsCust方法，插入hscom_application==插入项目appId=="+appId+";==appSql"+appSql);
	    	}else{
	    		String appSql = "update hscom_application set name=?, version=?, requirements=?, contract=?, cust_id=?, user_id=?,shortname=? where id=?";
	 	        pstmt = (PreparedStatement) conn.prepareStatement(appSql);
	 	        pstmt.setString(1, itemsPo.getItemname());
	 	        pstmt.setInt(2, itemsPo.getItemtype());
	 	        pstmt.setString(3, itemsPo.getItemrequire());
	 	        pstmt.setString(4, itemsPo.getContnum());
	 	        pstmt.setInt(5, custId);
	 	        pstmt.setInt(6, userId);
	 	        pstmt.setString(7, itemsPo.getItemname());
	 	        pstmt.setInt(8, appId);
	 	        pstmt.executeUpdate();
	 	        
	 	       logger.info("更新方法updateHsCust==更新项目appSql=="+appSql);
	    	}

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }finally{
	    	try{
	    		connClose(conn, pstmt, rs);
	    	}catch (SQLException e) {
		        e.printStackTrace();
		    }
	    }
	}
	
	/*
	 * 变更业务员
	 */
	public static void changeHsCustApp(SalesManPo srcSalePo, SalesManPo salePo,CustomerPo srcCustPo) {
		
	    Connection conn = getConn();

	    PreparedStatement pstmt = null;
	    
	    ResultSet rs = null; 
	    try {
	    	Integer srcUserId = getUserIdByParas(srcSalePo.getSalename(),srcSalePo.getSalephone());
	    	Integer userId = getUserIdByParas(salePo.getSalename(),salePo.getSalephone());
	    	Integer srcCustId = 0;
	    	if(null!=srcCustPo&&null!=srcCustPo.getRelatecustid()&&srcCustPo.getRelatecustid()>0){
	    		srcCustId = srcCustPo.getRelatecustid();
	    	}else{
	    		srcCustId = getCustIdByParas(srcCustPo.getContact(),srcCustPo.getPhone());
	    	}
	    	
    		String sql = "update hscom_customer set user_id=? where id=?";
    		pstmt = (PreparedStatement) conn.prepareStatement(sql);
    		pstmt.setInt(1, userId);
	        pstmt.setInt(2, srcCustId);
	        pstmt.executeUpdate();
	    	
	        logger.info("变更客户所有项目changeHsCustApp==变更客户sql=="+sql);
	        
    		String appSql = "update hscom_application set user_id=? where cust_id=? and user_id=?";
 	        pstmt = (PreparedStatement) conn.prepareStatement(appSql);
 	        pstmt.setInt(1, userId);
 	        pstmt.setInt(2, srcCustId);
 	        pstmt.setInt(3, srcUserId);
 	        pstmt.executeUpdate();
 	        
 	       logger.info("变更客户所有项目changeHsCustApp==变更所有项目appSql=="+appSql);

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }finally{
	    	try{
	    		connClose(conn, pstmt, rs);
	    	}catch (SQLException e) {
		        e.printStackTrace();
		    }
	    }
	}
	
	/*
	 * 变更业务员
	 */
	public static void changeHsAppOne(SalesManPo srcSalePo, SalesManPo salePo,ItemsPo itemPo) {
		
		if(itemPo.getItemtype()>5){
			return;
		}
	    Connection conn = getConn();

	    PreparedStatement pstmt = null;
	    
	    ResultSet rs = null; 
	    try {
//	    	Integer srcUserId = getUserIdByParas(srcSalePo.getSalename(),srcSalePo.getSalephone());
	    	Integer userId = getUserIdByParas(salePo.getSalename(),salePo.getSalephone());
	    	Integer appId = 0;
	    	if(null!=itemPo&&null!=itemPo.getRelateitemid()&&itemPo.getRelateitemid()>0){
	    		appId = itemPo.getRelateitemid();
	    	}else{
	    		HscomApplicationVo appVo = getAppVoByParas(itemPo.getItemname(),itemPo.getItemtype());
	    		appId = appVo.getId();
	    	}
	    	if(appId>0){
	    		String appSql = "update hscom_application set user_id=? where id=?";
	 	        pstmt = (PreparedStatement) conn.prepareStatement(appSql);
	 	        pstmt.setInt(1, userId);
	 	        pstmt.setInt(2, appId);
	 	        
	 	        logger.info("变更单个项目changeHsAppOne=="+"appId=="+appId+";==变更单个项目appSql=="+appSql);
	 	       pstmt.executeUpdate();
	    	}else{
	    		logger.info("变更单个项目changeHsAppOne==单个项目appId=="+appId+",不执行sql");
	    	}
 	       

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }finally{
	    	try{
	    		connClose(conn, pstmt, rs);
	    	}catch (SQLException e) {
		        e.printStackTrace();
		    }
	    }
	}


	public static List<ItemsPo> sysInputCrm(){
		String sql = null;  
	   	Connection conn = getConn();
	
	    PreparedStatement pstmt = null;
	    
	    ResultSet ret = null; 
	    String beginTime = FileUtil.getApplicationPro("system.application.time");
	    sql="select * from hscom_application hsapp where hsapp.addtime>='"+beginTime+"' ORDER BY id desc";
	    
	    List<ItemsPo> voList = new ArrayList<ItemsPo>();
	    try {
	    	pstmt = (PreparedStatement)conn.prepareStatement(sql);
			ret = pstmt.executeQuery();
			while (ret.next()) {
				int id = Integer.parseInt(ret.getString(1));
				String number = ret.getString(2);
				String name = ret.getString(3);
				String shortname = ret.getString(4);
				int version = Integer.parseInt(ret.getString(5));
				String ios = ret.getString(6);
				String appreciation = ret.getString(7);
				String android = ret.getString(8);
				String requirements = ret.getString(9);
				String color = ret.getString(10);
				String contract = ret.getString(11);
				String cust_id = ret.getString(12);
				String user_id = ret.getString(13);
				String addtime = ret.getString(14).trim();  //2017-09-23 11:47:18.0
				int status = Integer.parseInt(ret.getString(15));
				String domain = ret.getString(16);
				String iscustomize = ret.getString(17);
				String desc = ret.getString(18);
				String part_id = ret.getString(19);
				String is_english = ret.getString(20);
				int year = Integer.parseInt(addtime.substring(0, 4));
				int month = Integer.parseInt(addtime.substring(5,7));
				if(year==2017 && month>=8){
					if(version>0){
						
				    	ItemsPo itemsPo = new ItemsPo();
				    	itemsPo.setItemname(name);
				    	itemsPo.setItemtype(version);
				    	itemsPo.setItemstatus(2);
				    	itemsPo.setContnum(contract);
				    	itemsPo.setItemrequire(requirements);
				    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				    	itemsPo.setCreatedate(sdf.parse(addtime));
				    	itemsPo.setStandby1(shortname);
				    	itemsPo.setRelateitemid(id);
				    	itemsPo.setRelatecontstatus(status);
				    	itemsPo.setRelatedesc(desc);
				    	itemsPo.setStandby3(number);
				    	itemsPo.setCustid(Integer.valueOf(cust_id));
				    	voList.add(itemsPo);
					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally {
			try{
	    		connClose(conn, pstmt, ret);
	    	}catch (SQLException e) {
		        e.printStackTrace();
		    }
		}
		return voList;
   }
   
   //根据hscom_application表中的custid去查询客户信息，在crm系统中的customer中看是否有，如果有，返回custid ，如果没有插入客户信息返回custid
   public static CustomerPo getCustPo(int cust_id,Date addtime){
   	 String sql = null;  
 	 Connection conn = getConn();
	
     PreparedStatement pstmt = null;
    
   	 ResultSet ret = null;
   	 List<Integer> list = new ArrayList<Integer>();
   	 sql="SELECT * from hscom_customer WHERE id="+cust_id;
   	 
   	 CustomerPo custPo = null;
    	try {
    		pstmt = (PreparedStatement)conn.prepareStatement(sql);
			ret = pstmt.executeQuery();
			while (ret.next()) {
				int id = Integer.parseInt(ret.getString(1));
				String company_name = ret.getString(2);
				String telephone = ret.getString(3);
				String fax = ret.getString(4);
				String site_url = ret.getString(5);
				String company_email = ret.getString(6);
				String company_address = ret.getString(7);
				String linkman = ret.getString(8);
				String degree = ret.getString(9);
				String mobile = ret.getString(10);
				String qq = ret.getString(11);
				String summary = ret.getString(12);
				String user_id = ret.getString(13);
				int status = Integer.parseInt(ret.getString(14));
				if(status<0){
					status=2;
				}
				String part_id = ret.getString(15);
				
				custPo = new CustomerPo();
				custPo.setCompanyname(company_name);
				custPo.setContact(linkman);
				custPo.setPhone(mobile);
				custPo.setEmail(company_email);
				custPo.setAddress(company_address);
				custPo.setCuststatus(status);
				custPo.setCreatedate(addtime);
				custPo.setRelatecustid(id);
				custPo.setSaleid(Integer.valueOf(user_id));
			}
			ret.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			try{
	    		connClose(conn, pstmt, ret);
	    	}catch (SQLException e) {
		        e.printStackTrace();
		    }
		}
    	return custPo;
   }
   
   //根据system user_id 获取crm的salename，如果没有，返回0
   public static String getsalename(int userid){
   	 String sql = null;  
   	 Connection conn = getConn();
     PreparedStatement pstmt = null;
  	 ResultSet ret = null;
   	 String salename = null;
   	 sql="SELECT * from hscom_user WHERE id="+userid;
    	try {
    		pstmt = (PreparedStatement)conn.prepareStatement(sql);
			ret = pstmt.executeQuery();
			while (ret.next()) {
				String degree = ret.getString(5).trim();
				salename = degree;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			try{
	    		connClose(conn, pstmt, ret);
	    	}catch (SQLException e) {
		        e.printStackTrace();
		    }
		}
    	return salename;
   }
	
}
