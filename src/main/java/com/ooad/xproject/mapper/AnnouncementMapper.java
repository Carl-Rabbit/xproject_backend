package com.ooad.xproject.mapper;

import com.ooad.xproject.entity.Announcement;

import java.util.List;

public interface AnnouncementMapper {
    int deleteByPrimaryKey(Integer annId);

    int insert(Announcement record);

    int insertSelective(Announcement record);

    Announcement selectByPrimaryKey(Integer annId);

    List<Announcement> selectByProjId(Integer projId);

    int updateByPrimaryKeySelective(Announcement record);

    int updateByPrimaryKey(Announcement record);
}