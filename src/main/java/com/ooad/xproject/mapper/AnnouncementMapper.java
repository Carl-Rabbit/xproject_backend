package com.ooad.xproject.mapper;

import com.ooad.xproject.entity.Announcement;

import java.util.List;

public interface AnnouncementMapper {
    int deleteByPrimaryKey(Integer annId);

    int insert(Announcement record);

    int insertSelective(Announcement record);

    Announcement selectByPrimaryKey(Integer annId);

    int updateByPrimaryKeySelective(Announcement record);

    int updateByPrimaryKey(Announcement record);

    // extra

    List<Announcement> selectByProjId(Integer projId);
}