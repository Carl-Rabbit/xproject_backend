package com.ooad.xproject.bo;

import com.ooad.xproject.constant.MsgType;
import com.ooad.xproject.entity.Message;
import com.ooad.xproject.entity.ProjectInst;

import java.util.HashMap;
import java.util.Map;

public class MessageFactory {
    private static final Map<MsgType, String> titleMap;

    static {
        titleMap = new HashMap<>();
        titleMap.put(MsgType.Apply, "apply to join your team");
        titleMap.put(MsgType.JoinSuccess, "successfully joined your team");
        titleMap.put(MsgType.JoinFail, "has been refused to join your team");
        titleMap.put(MsgType.Quit, "quited from your team");
        titleMap.put(MsgType.Invite, "invited you to join his/her team");
        titleMap.put(MsgType.InviteSuccess, "agreed to join your team");
        titleMap.put(MsgType.InviteFail, "rejected to join your team");
    }

    public static Message createInviteMessage(int creatorRoleId, ProjectInst projInst, int inviteStdRoleId) {
        Message msg = new Message();
        msg.setProjId(projInst.getProjId());
        msg.setProjInstId(projInst.getProjInstId());
        msg.setType(MsgType.Invite.name());
        msg.setTitle(titleMap.get(MsgType.Invite));

        msg.setRoleId(inviteStdRoleId);
        msg.setCreatorRoleId(creatorRoleId);
        msg.setConfirmed(false);
        msg.setDecided(false);

        return msg;
    }

    public static Message createApplyTeamMsg(ProjectInst projInst, Integer applyRoleId, String message) {
        Message msg = new Message();
        msg.setProjId(projInst.getProjId());
        msg.setProjInstId(projInst.getProjInstId());
        msg.setType(MsgType.Apply.name());
        msg.setTitle(titleMap.get(MsgType.Apply));

        msg.setContent(message);

        msg.setCreatorRoleId(applyRoleId);
        msg.setConfirmed(false);
        msg.setDecided(false);

        return msg;
    }

    public static Message createApplyReplyMsg(Integer applicantRoleId, Integer handlerRoleId, boolean accepted) {
        Message msg = new Message();

        MsgType type = accepted ? MsgType.JoinSuccess : MsgType.JoinFail;
        msg.setType(type.name());
        msg.setTitle(titleMap.get(type));

        msg.setCreatorRoleId(handlerRoleId);
        msg.setHandlerRoleId(handlerRoleId);
        msg.setRoleId(applicantRoleId);

        msg.setConfirmed(false);
        msg.setDecided(false);

        return msg;
    }

    public static Message createInviteReplyMsg(Integer projId, Integer projInstId,
                                               Integer handlerRoleId, boolean accepted) {
        Message msg = new Message();

        msg.setProjId(projId);
        msg.setProjInstId(projInstId);

        MsgType type = accepted ? MsgType.InviteSuccess : MsgType.InviteFail;
        msg.setType(type.name());
        msg.setTitle(titleMap.get(type));

        msg.setCreatorRoleId(handlerRoleId);
        msg.setHandlerRoleId(handlerRoleId);
        msg.setConfirmed(false);
        msg.setDecided(false);

        return msg;
    }
}
