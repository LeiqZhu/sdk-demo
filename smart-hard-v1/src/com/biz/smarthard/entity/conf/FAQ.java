package com.biz.smarthard.entity.conf;

import com.biz.smarthard.bean.SHConfig;
import com.biz.smarthard.bean.SHDbsql;
import com.biz.smarthard.entity.SHEntity;
import com.biz.smarthard.savedb.Dbsql;
import com.biz.smarthard.utils.ReloadUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class FAQ implements SHEntity<FAQ> {
    private static final Logger log = LoggerFactory.getLogger(FAQ.class);

    private static final long serialVersionUID = 1L;

    private String question;

    private String answer;

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    @Override
    public FAQ Map2Bean(Map<String, Object> map) {
        return null;
    }

    @Override
    public void reloadTable() {
        new ReloadUtil<>(
                SHDbsql.FAQ.QUERYAQ_ALL_SQL,
                SHConfig.AQ,
                FAQ.class,
                "project"
        ).reloadList(false);
    }

    public static void main(String[] args) {
        new FAQ().reloadTable();
    }
}
