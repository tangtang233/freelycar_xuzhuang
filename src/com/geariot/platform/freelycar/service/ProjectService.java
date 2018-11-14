package com.geariot.platform.freelycar.service;

import com.geariot.platform.freelycar.dao.CardDao;
import com.geariot.platform.freelycar.dao.ProjectDao;
import com.geariot.platform.freelycar.dao.ServiceDao;
import com.geariot.platform.freelycar.entities.Project;
import com.geariot.platform.freelycar.entities.Project.ProjectInner;
import com.geariot.platform.freelycar.model.RESCODE;
import com.geariot.platform.freelycar.utils.Constants;
import com.geariot.platform.freelycar.utils.JsonResFactory;
import net.sf.json.JsonConfig;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


@Service
@Transactional
public class ProjectService {

    public final static int SOLD_OUT = 0;
    public final static int ON_SALE = 1;

    @Autowired
    private ProjectDao projectDao;

    @Autowired
    private CardDao cardDao;

    @Autowired
    private ServiceDao serviceDao;

    public String addProject(Project project) {
        int id = project.getId();
        if (id == 0) {
            Project exist = projectDao.findProjectByName(project.getName());
            if (exist != null) {
                return JsonResFactory.buildOrg(RESCODE.NAME_EXIST).toString();
            }
        }

        project.setCreateDate(new Date());
        projectDao.save(project);
        JsonConfig config = JsonResFactory.dateConfig();
        return JsonResFactory.buildNetWithData(RESCODE.SUCCESS,
                net.sf.json.JSONObject.fromObject(project, config)).toString();
    }

    public String deleteProject(Integer... projectIds) {
        List<Integer> ids = Arrays.asList(projectIds);
        // 查找CardProjectRemainingInfo和ServiceProjectInfo，如果有该project的引用，则返回错误，删除失败
        if (this.cardDao.countProjectByIds(ids) > 0
                || this.serviceDao.countProjectByIds(ids) > 0) {
            return JsonResFactory.buildOrg(RESCODE.UNABLE_TO_DELETE).toString();
        }

        int count = 0;
        for (int projectId : projectIds) {
            if (projectDao.findProjectById(projectId) == null) {
                count++;
            } else {
                projectDao.delete(projectId);
                projectDao.deleteInventory(projectId);
            }
        }
        if (count != 0) {
            String tips = "共" + count + "条未在数据库中存在记录";
            net.sf.json.JSONObject obj = JsonResFactory.buildNetWithData(
                    RESCODE.PART_SUCCESS, tips);
            long realSize = projectDao.getCount();
            obj.put(Constants.RESPONSE_REAL_SIZE_KEY, realSize);
            return obj.toString();
        } else {
            JSONObject obj = JsonResFactory.buildOrg(RESCODE.SUCCESS);
            long realSize = projectDao.getCount();
            obj.put(Constants.RESPONSE_REAL_SIZE_KEY, realSize);
            return obj.toString();
        }
    }

    public Map<String, Object> getSelectProject(String name, String programId, int page, int number) {
        int from = (page - 1) * number;
        List<Project> list = projectDao.getConditionQuery(name, programId, from, number);
        if (list == null || list.isEmpty()) {
            return RESCODE.NO_RECORD.getJSONRES();
        }
        long realSize = (long) projectDao.getConditionCount(name, programId);
        int size = (int) Math.ceil(realSize / (double) number);
        return RESCODE.SUCCESS.getJSONRES(list, size, realSize);
    }

    public String modifyProject(Project project) {
        Project exist = projectDao.findProjectById(project.getId());
        if (exist == null) {
            return JsonResFactory.buildOrg(RESCODE.NO_RECORD).toString();
        }
        exist.setName(project.getName());
        exist.setComment(project.getComment());
        exist.setPrice(project.getPrice());
        exist.setPricePerUnit(project.getPricePerUnit());
        exist.setReferWorkTime(project.getReferWorkTime());
        return JsonResFactory.buildOrg(RESCODE.SUCCESS).toString();
    }

    public Map<String, Object> getProjectName() {
        List<Object[]> exists = projectDao.getProjectName();
        if (exists == null || exists.isEmpty()) {
            return RESCODE.NO_RECORD.getJSONRES();
        } else {
            List<ProjectInner> projectBeans = new ArrayList<>();
            for (Object[] exist : exists) {
                projectBeans.add(new ProjectInner(Integer.valueOf(String
                        .valueOf(exist[0])), String.valueOf(exist[1])));
            }
            return RESCODE.SUCCESS.getJSONRES(projectBeans);
        }
    }

    public String getProject(int projectId) {
        Project exist = projectDao.findProjectById(projectId);
        if (exist == null) {
            return JsonResFactory.buildOrg(RESCODE.NO_RECORD).toString();
        } else {
            JsonConfig config = JsonResFactory.dateConfig();
            return JsonResFactory.buildNetWithData(RESCODE.SUCCESS,
                    net.sf.json.JSONObject.fromObject(exist, config))
                    .toString();
        }
    }

    /**
     * 服务项目在智能柜上架/下架
     *
     * @param id 服务项目id
     * @return string
     */
    public String onSaleOrSoldOut(Integer id, int saleStatus) {
        if (null == id) {
            return JsonResFactory.buildOrg(RESCODE.WRONG_PARAM).toString();
        }
        Project project = projectDao.findProjectById(id);
        if (null == project) {
            return JsonResFactory.buildOrg(RESCODE.NOT_FOUND).toString();
        }
        if (ON_SALE == saleStatus) {
            project.setSaleStatus(ON_SALE);
        } else {
            project.setSaleStatus(SOLD_OUT);
        }
        projectDao.saveOrUpdate(project);

        JsonConfig config = JsonResFactory.dateConfig();
        return JsonResFactory.buildNetWithData(RESCODE.SUCCESS,
                net.sf.json.JSONObject.fromObject(project, config))
                .toString();
    }
}
