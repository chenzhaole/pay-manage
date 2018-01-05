package com.sys.admin.common.persistence;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sys.admin.common.config.GlobalConfig;
import com.sys.admin.common.utils.CookieUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;

/**
 * 分页类
 *
 */
public class Page<T> implements Serializable {

    private static final long serialVersionUID = -1765624036347738275L;
    private int pageNo = 1; // 当前页码
    private int pageSize = Integer.valueOf(GlobalConfig.getConfig("page.pageSize")); // 页面大小，设置为“-1”表示不进行分页（分页无效）

    private long count; // 总记录数，设置为“-1”表示不查询总数

    private int first; // 首页索引
    private int last; // 尾页索引
    private int prev; // 上一页索引
    private int next; // 下一页索引
    
    private int start = 0;
    private int end;

    private boolean firstPage; //是否是第一页
    private boolean lastPage; //是否是最后一页

    private int length = 8; // 显示页面长度
    private int slider = 1; // 前后显示页面长度

    private List<T> list = new ArrayList<T>();

    private String orderBy = ""; // 标准查询有效， 实例： updatedate desc, name asc

    private String funcName = "page"; // 设置点击页码调用的js函数名称，默认为page，在一页有多个分页对象时使用。

    private String message = ""; // 设置提示消息，显示在“共n条”之后

    /**
     * 构造方法
     *
     * @param request  传递 repage 参数，来记住页码
     * @param response 用于设置 Cookie，记住页码
     */
    public Page(HttpServletRequest request, HttpServletResponse response) {
        this(request, response, -2);
    }

    /**
     * 构造方法
     *
     * @param request  传递 repage 参数，来记住页码
     * @param response 用于设置 Cookie，记住页码
     */
    public Page(HttpServletRequest request, HttpServletResponse response, boolean flag) {
        this(request, response, -2, flag);
    }

    /**
     * 构造方法
     *
     * @param request  传递 repage 参数，来记住页码
     * @param response 用于设置 Cookie，记住页码
     * @param pageSize 分页大小，如果传递 -1 则为不分页，返回所有数据
     */
    public Page(HttpServletRequest request, HttpServletResponse response, int pageSize) {
        // 设置页码参数（传递repage参数，来记住页码）
        String no = request.getParameter("pageNo");
        if (StringUtils.isNumeric(no)) {
            CookieUtils.setCookie(response, "pageNo", no);
            this.setPageNo(Integer.parseInt(no));
        } else if (request.getParameter("repage") != null) {
            no = CookieUtils.getCookie(request, "pageNo");
            if (StringUtils.isNumeric(no)) {
                this.setPageNo(Integer.parseInt(no));
            }
        }
        // 设置页面大小参数（传递repage参数，来记住页码大小）
        String size = request.getParameter("pageSize");
        if (StringUtils.isNumeric(size)) {
            CookieUtils.setCookie(response, "pageSize", size);
            this.setPageSize(Integer.parseInt(size));
        } else if (request.getParameter("repage") != null) {
            no = CookieUtils.getCookie(request, "pageSize");
            if (StringUtils.isNumeric(size)) {
                this.setPageSize(Integer.parseInt(no));
            }
        }

        if (pageSize != -2) {
            this.pageSize = pageSize;
        }

        // 设置排序参数
        String orderBy = request.getParameter("orderBy");
        if (StringUtils.isNotBlank(orderBy)) {
            this.setOrderBy(orderBy);
        }

        PageHelper.startPage(this.getPageNo(), this.getPageSize());
    }

    /**
     * 构造方法
     *
     * @param request  传递 repage 参数，来记住页码
     * @param response 用于设置 Cookie，记住页码
     * @param pageSize 分页大小，如果传递 -1 则为不分页，返回所有数据
     */
    public Page(HttpServletRequest request, HttpServletResponse response, int pageSize, boolean flag) {
        // 设置页码参数（传递repage参数，来记住页码）
        String no = request.getParameter("pageNo");
        if (StringUtils.isNumeric(no)) {
            CookieUtils.setCookie(response, "pageNo", no);
            this.setPageNo(Integer.parseInt(no));
        } else if (request.getParameter("repage") != null) {
            no = CookieUtils.getCookie(request, "pageNo");
            if (StringUtils.isNumeric(no)) {
                this.setPageNo(Integer.parseInt(no));
            }
        }
        // 设置页面大小参数（传递repage参数，来记住页码大小）
        String size = request.getParameter("pageSize");
        if (StringUtils.isNumeric(size)) {
            CookieUtils.setCookie(response, "pageSize", size);
            this.setPageSize(Integer.parseInt(size));
        } else if (request.getParameter("repage") != null) {
            no = CookieUtils.getCookie(request, "pageSize");
            if (StringUtils.isNumeric(size)) {
                this.setPageSize(Integer.parseInt(no));
            }
        }

        if (pageSize != -2) {
            this.pageSize = pageSize;
        }

        // 设置排序参数
        String orderBy = request.getParameter("orderBy");
        if (StringUtils.isNotBlank(orderBy)) {
            this.setOrderBy(orderBy);
        }
    }

    /**
     * 构造方法
     *
     * @param pageNo   当前页码
     * @param pageSize 分页大小
     */
    public Page(int pageNo, int pageSize) {
        this(pageNo, pageSize, 0, true);
    }

    /**
     * 构造方法
     *
     * @param pageNo   当前页码
     * @param pageSize 分页大小
     */
    public Page(int pageNo, int pageSize, boolean flag) {
        this(pageNo, pageSize, 0, flag);
    }

    /**
     * 构造方法
     *
     * @param pageNo   当前页码
     * @param pageSize 分页大小
     * @param count    数据条数
     */
    public Page(int pageNo, int pageSize, long count, boolean flag) {
        this(pageNo, pageSize, count, new ArrayList<T>(), flag);
    }

    /**
     * 构造方法
     *
     * @param pageNo   当前页码
     * @param pageSize 分页大小
     * @param count    数据条数
     * @param list     本页数据对象列表
     */
    public Page(int pageNo, int pageSize, long count, List<T> list, boolean flag) {
        this.setCount(count);
        this.setPageNo(pageNo);
        this.pageSize = pageSize;
        this.setList(list);

        if (flag) {
            PageHelper.startPage(this.getPageNo(), this.getPageSize());
        }
    }

    public Page() {

    }

    /**
     * 初始化参数
     */
    public void initialize() {

        //1
        this.first = 1;

        this.last = (int) (getCount() / (this.pageSize < 1 ? 20 : this.pageSize) + first - 1);

        if (this.count % this.pageSize != 0 || this.last == 0) {
            this.last++;
        }

        if (this.last < this.first) {
            this.last = this.first;
        }

        if (this.pageNo <= 1) {
            this.pageNo = this.first;
            this.firstPage = true;
        }

        if (this.pageNo >= this.last) {
            this.pageNo = this.last;
            this.lastPage = true;
        }

        if (this.pageNo < this.last - 1) {
            this.next = this.pageNo + 1;
        } else {
            this.next = this.last;
        }

        if (this.pageNo > 1) {
            this.prev = this.pageNo - 1;
        } else {
            this.prev = this.first;
        }

        //2
        if (this.pageNo < this.first) {// 如果当前页小于首页
            this.pageNo = this.first;
        }

        if (this.pageNo > this.last) {// 如果当前页大于尾页
            this.pageNo = this.last;
        }
        
        if(this.pageNo>0){
          start = (this.pageNo - 1) * this.pageSize;
        }
        end = this.pageNo * pageSize;
        end = end <= this.count ? end : (int)this.count;

    }

    /**
     * 默认输出当前分页标签
     * <div class="page">${page}</div>
     */
    @Override
    public String toString() {

        initialize();
        StringBuilder sb = new StringBuilder();

        if (pageNo == first) {// 如果是首页
            sb.append("<li class=\"disabled\"><a href=\"javascript:\">&#171; 上一页</a></li>\n");
        } else {
            sb.append("<li><a href=\"javascript:" + funcName + "(" + prev + "," + pageSize + ");\">&#171; 上一页</a></li>\n");
        }

        int begin = pageNo - (length / 2);

        if (begin < first) {
            begin = first;
        }

        int end = begin + length - 1;

        if (end >= last) {
            end = last;
            begin = end - length + 1;
            if (begin < first) {
                begin = first;
            }
        }

        if (begin > first) {
            int i = 0;
            for (i = first; i < first + slider && i < begin; i++) {
                sb.append("<li><a href=\"javascript:" + funcName + "(" + i + "," + pageSize + ");\">"
                        + (i + 1 - first) + "</a></li>\n");
            }
            if (i < begin) {
                sb.append("<li class=\"disabled\"><a href=\"javascript:\">...</a></li>\n");
            }
        }

        for (int i = begin; i <= end; i++) {
            if (i == pageNo) {
                sb.append("<li class=\"active\"><a href=\"javascript:\">" + (i + 1 - first)
                        + "</a></li>\n");
            } else {
                sb.append("<li><a href=\"javascript:" + funcName + "(" + i + "," + pageSize + ");\">"
                        + (i + 1 - first) + "</a></li>\n");
            }
        }

        if (last - end > slider) {
            sb.append("<li class=\"disabled\"><a href=\"javascript:\">...</a></li>\n");
            end = last - slider;
        }

        for (int i = end + 1; i <= last; i++) {
            sb.append("<li><a href=\"javascript:" + funcName + "(" + i + "," + pageSize + ");\">"
                    + (i + 1 - first) + "</a></li>\n");
        }

        if (pageNo == last) {
            sb.append("<li class=\"disabled\"><a href=\"javascript:\">下一页 &#187;</a></li>\n");
        } else {
            sb.append("<li><a href=\"javascript:" + funcName + "(" + next + "," + pageSize + ");\">"
                    + "下一页 &#187;</a></li>\n");
        }

        sb.append("<li class=\"disabled controls\"><a href=\"javascript:\">当前 ");
        sb.append("<input disabled=\"disabled\" type=\"text\"  value=\"" + pageNo + "\" onkeypress=\"__pageNoChange(event,this)\"");
        sb.append(" onclick=\"this.select();\"/> / ");
        float pageNumber = Float.valueOf(getCount()) / Float.valueOf(pageSize);
        if (pageNumber < 1) {
            pageNumber = 1;
        }
        sb.append("<input type=\"text\" disabled=\"disabled\" value=\"" + (int) Math.ceil(pageNumber) + "\" onkeypress=\"__pageSizeChange(event,this)\"");
        sb.append(" onclick=\"this.select();\"/> 页，");
        sb.append("共 " + count + " 条" + (message != null ? message : "") + "</a><li>\n");

        sb.insert(0, "<ul>\n").append("</ul>\n");

        sb.append("<script>\n");
        sb.append("function __pageNoChange(e, __this){var evt=e||window.event||__this;var c=e.keyCode||e.which;");
        sb.append("if(c==13) " + funcName + "(__this.value," + pageSize + ");}\n");
        sb.append("function __pageSizeChange(e, __this){var evt=e||window.event||__this;var c=e.keyCode||e.which;");
        sb.append("if(c==13) " + funcName + "(" + pageNo + ",__this.value);}\n");
        sb.append("</script>\n");

        sb.append("<div style=\"clear:both;\"></div>");

//		sb.insert(0,"<div class=\"page\">\n").append("</div>\n");

        return sb.toString();
    }
    public int getStart(){
    	return start;
    }
    public int getEnd(){
    	return end;
    }
    /**
     * 获取设置总数
     *
     * @return 数据总数
     */
    public long getCount() {
        return count;
    }

    /**
     * 设置数据总数
     *
     * @param count 数据总数
     */
    public void setCount(long count) {
        this.count = count;
        if (pageSize >= count) {
            pageNo = 1;
        }
        this.initialize();
    }

    /**
     * 获取当前页码
     *
     * @return 当前页码
     */
    public int getPageNo() {
        return pageNo;
    }

    /**
     * 设置当前页码
     *
     * @param pageNo 当前页码
     */
    private void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    /**
     * 获取页面大小
     *
     * @return 页面大小
     */
    public int getPageSize() {
        return pageSize;
    }

    /**
     * 设置页面大小（最大1000）
     *
     * @param pageSize 页面大小
     */
    private void setPageSize(int pageSize) {
        this.pageSize = pageSize <= 0 ? 10 : pageSize > 1000 ? 1000 : pageSize;
    }

    /**
     * 首页索引
     *
     * @return 首页索引
     */
    public int getFirst() {
        return first;
    }

    /**
     * 尾页索引
     *
     * @return 尾页索引
     */
    public int getLast() {
        return last;
    }

    /**
     * 获取页面总数
     *
     * @return getLast();
     */
    public int getTotalPage() {
        return getLast();
    }

    /**
     * 是否为第一页
     *
     * @return 是最第一页：true；不是第一页：false
     */
    public boolean isFirstPage() {
        return firstPage;
    }

    /**
     * 是否为最后一页
     *
     * @return 是最后一页：true；不是最后一页：false
     */
    public boolean isLastPage() {
        return lastPage;
    }

    /**
     * 上一页索引值
     *
     * @return 上一页索引值
     */
    public int getPrev() {
        if (isFirstPage()) {
            return pageNo;
        } else {
            return pageNo - 1;
        }
    }

    /**
     * 下一页索引值
     *
     * @return 下一页索引值
     */
    public int getNext() {
        if (isLastPage()) {
            return pageNo;
        } else {
            return pageNo + 1;
        }
    }

    /**
     * 获取本页数据对象列表
     *
     * @return List<T>
     */
    public List<T> getList() {
        return list;
    }

    /**
     * 设置本页数据对象列表
     *
     * @param list 数据对象列表
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void setList(List<T> list) {
        this.list = list;
        PageInfo pageInfo = new PageInfo(list);
        if (pageInfo.getList() != null) {
            if (getCount() == 0) {
                setCount(pageInfo.getTotal());
            }

            this.initialize();
        }
    }

    /**
     * 获取查询排序字符串
     *
     * @return 排序字符串
     */
    public String getOrderBy() {
        return orderBy;
    }

    /**
     * 设置查询排序，标准查询有效， 实例： updatedate desc, name asc
     */
    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    /**
     * 获取点击页码调用的js函数名称
     * function ${page.funcName}(pageNo){location="${ctx}/list-${category.id}${urlSuffix}?pageNo="+i;}
     *
     * @return js函数名称
     */
    public String getFuncName() {
        return funcName;
    }

    /**
     * 设置点击页码调用的js函数名称，默认为page，在一页有多个分页对象时使用。
     *
     * @param funcName 默认为page
     */
    public void setFuncName(String funcName) {
        this.funcName = funcName;
    }

    /**
     * 设置提示消息，显示在“共n条”之后
     *
     * @param message 提示消息
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * 分页是否有效
     *
     * @return this.pageSize==-1
     */
    public boolean isDisabled() {
        return this.pageSize == -1;
    }

    /**
     * 是否进行总数统计
     *
     * @return this.count==-1
     */
    public boolean isNotCount() {
        return this.count == -1;
    }

    /**
     * 获取 Hibernate FirstResult
     */
    public int getFirstResult() {
        int firstResult = (getPageNo() - 1) * getPageSize();
        if (firstResult >= getCount()) {
            firstResult = 0;
        }
        return firstResult;
    }

    /**
     * 获取 Hibernate MaxResults
     */
    public int getMaxResults() {
        return getPageSize();
    }

    /**
     * 获取 Spring data JPA 分页对象
     */
    public Pageable getSpringPage() {
        List<Order> orders = new ArrayList<Order>();
        if (orderBy != null) {
            for (String order : StringUtils.split(orderBy, ",")) {
                String[] o = StringUtils.split(order, " ");
                if (o.length == 1) {
                    orders.add(new Order(Direction.ASC, o[0]));
                } else if (o.length == 2) {
                    if ("DESC".equals(o[1].toUpperCase())) {
                        orders.add(new Order(Direction.DESC, o[0]));
                    } else {
                        orders.add(new Order(Direction.ASC, o[0]));
                    }
                }
            }
        }
        return new PageRequest(this.pageNo - 1, this.pageSize, new Sort(orders));
    }

    /**
     * 设置 Spring data JPA 分页对象，转换为本系统分页对象
     */
    public void setSpringPage(org.springframework.data.domain.Page<T> page) {
        this.pageNo = page.getNumber();
        this.pageSize = page.getSize();
        this.count = page.getTotalElements();
        this.list = page.getContent();
    }

}
