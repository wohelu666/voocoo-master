package com.voocoo.pet.entity;

import java.util.ArrayList;
import java.util.List;

public class MomentDto {
    private long id;
    private boolean official;
    private long userId;
    private String nickName;
    private String avatarUrl;
    private boolean isFriend;
    private int fansNum;
    private int momentType;
    private String moment;
    private String sourceUrl;
    private int status;
    private int isShow;
    private String remark;
    private int commentNum;
    private int priseNum;
    private String ctime;
    private String imgUrl;
    private List<MomentPriseDto> priseList = new ArrayList<>();
    private MomentCommentDto comment;

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isOfficial() {
        return official;
    }

    public void setOfficial(boolean official) {
        this.official = official;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public boolean isFriend() {
        return isFriend;
    }

    public void setFriend(boolean friend) {
        isFriend = friend;
    }

    public int getFansNum() {
        return fansNum;
    }

    public void setFansNum(int fansNum) {
        this.fansNum = fansNum;
    }

    public int getMomentType() {
        return momentType;
    }

    public void setMomentType(int momentType) {
        this.momentType = momentType;
    }

    public String getMoment() {
        return moment;
    }

    public void setMoment(String moment) {
        this.moment = moment;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getIsShow() {
        return isShow;
    }

    public void setIsShow(int isShow) {
        this.isShow = isShow;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getCommentNum() {
        return commentNum;
    }

    public void setCommentNum(int commentNum) {
        this.commentNum = commentNum;
    }

    public int getPriseNum() {
        return priseNum;
    }

    public void setPriseNum(int priseNum) {
        this.priseNum = priseNum;
    }

    public String getCtime() {
        return ctime;
    }

    public void setCtime(String ctime) {
        this.ctime = ctime;
    }

    public List<MomentPriseDto> getPriseList() {
        return priseList;
    }

    public void setPriseList(List<MomentPriseDto> priseList) {
        this.priseList = priseList;
    }

    public MomentCommentDto getComment() {
        return comment;
    }

    public void setComment(MomentCommentDto comment) {
        this.comment = comment;
    }

    public class MomentPriseDto {
        private long momentId;
        private long userId;
        private String nickName;
        private String avatarUrl;

        public long getMomentId() {
            return momentId;
        }

        public void setMomentId(long momentId) {
            this.momentId = momentId;
        }

        public long getUserId() {
            return userId;
        }

        public void setUserId(long userId) {
            this.userId = userId;
        }

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }

        public String getAvatarUrl() {
            return avatarUrl;
        }

        public void setAvatarUrl(String avatarUrl) {
            this.avatarUrl = avatarUrl;
        }
    }

    public class MomentCommentDto {
        private String nextId;
        private boolean nextValid;
        private List<MomentCommentRecords> records = new ArrayList<>();

        public String getNextId() {
            return nextId;
        }

        public void setNextId(String nextId) {
            this.nextId = nextId;
        }

        public boolean isNextValid() {
            return nextValid;
        }

        public void setNextValid(boolean nextValid) {
            this.nextValid = nextValid;
        }

        public List<MomentCommentRecords> getRecords() {
            return records;
        }

        public void setRecords(List<MomentCommentRecords> records) {
            this.records = records;
        }

        public class MomentCommentRecords {
            private String avatarUrl;
            private boolean canDel;
            private String comment;
            private String ctime;
            private String id;
            private String isDeleted;
            private int isResp;
            private int isShow;
            private String momentId;
            private String nickName;
            private String operator;
            private String operatorId;
            private int priseNum;
            private String remark;
            private String respCommentId;
            private String userId;
            private String utime;
            private RespCommentData respCommentData;

            public String getAvatarUrl() {
                return avatarUrl;
            }

            public void setAvatarUrl(String avatarUrl) {
                this.avatarUrl = avatarUrl;
            }

            public boolean isCanDel() {
                return canDel;
            }

            public void setCanDel(boolean canDel) {
                this.canDel = canDel;
            }

            public String getComment() {
                return comment;
            }

            public void setComment(String comment) {
                this.comment = comment;
            }

            public String getCtime() {
                return ctime;
            }

            public void setCtime(String ctime) {
                this.ctime = ctime;
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getIsDeleted() {
                return isDeleted;
            }

            public void setIsDeleted(String isDeleted) {
                this.isDeleted = isDeleted;
            }

            public int getIsResp() {
                return isResp;
            }

            public void setIsResp(int isResp) {
                this.isResp = isResp;
            }

            public int getIsShow() {
                return isShow;
            }

            public void setIsShow(int isShow) {
                this.isShow = isShow;
            }

            public String getMomentId() {
                return momentId;
            }

            public void setMomentId(String momentId) {
                this.momentId = momentId;
            }

            public String getNickName() {
                return nickName;
            }

            public void setNickName(String nickName) {
                this.nickName = nickName;
            }

            public String getOperator() {
                return operator;
            }

            public void setOperator(String operator) {
                this.operator = operator;
            }

            public String getOperatorId() {
                return operatorId;
            }

            public void setOperatorId(String operatorId) {
                this.operatorId = operatorId;
            }

            public int getPriseNum() {
                return priseNum;
            }

            public void setPriseNum(int priseNum) {
                this.priseNum = priseNum;
            }

            public String getRemark() {
                return remark;
            }

            public void setRemark(String remark) {
                this.remark = remark;
            }

            public String getRespCommentId() {
                return respCommentId;
            }

            public void setRespCommentId(String respCommentId) {
                this.respCommentId = respCommentId;
            }

            public String getUserId() {
                return userId;
            }

            public void setUserId(String userId) {
                this.userId = userId;
            }

            public String getUtime() {
                return utime;
            }

            public void setUtime(String utime) {
                this.utime = utime;
            }

            public RespCommentData getRespCommentData() {
                return respCommentData;
            }

            public void setRespCommentData(RespCommentData respCommentData) {
                this.respCommentData = respCommentData;
            }

            public class RespCommentData {
                private String nextId;
                private boolean nextValid;
                private List<RespCommentRecords> records = new ArrayList<>();

                public String getNextId() {
                    return nextId;
                }

                public void setNextId(String nextId) {
                    this.nextId = nextId;
                }

                public boolean isNextValid() {
                    return nextValid;
                }

                public void setNextValid(boolean nextValid) {
                    this.nextValid = nextValid;
                }

                public List<RespCommentRecords> getRecords() {
                    return records;
                }

                public void setRecords(List<RespCommentRecords> records) {
                    this.records = records;
                }

                public class RespCommentRecords {
                    private String ancestor;
                    private String avatarUrl;
                    private boolean canDel;
                    private String comment;
                    private String ctime;
                    private String descendant;
                    private String distance;
                    private String id;
                    private String isDeleted;
                    private String isResp;
                    private String isShow;
                    private String momentId;
                    private String nickName;
                    private String operator;
                    private String operatorId;
                    private int priseNum;
                    private String relationId;
                    private String remark;
                    private String respAvatarUrl;
                    private String respCommentId;
                    private String respNickName;
                    private String userId;
                    private String utime;

                    public String getAncestor() {
                        return ancestor;
                    }

                    public void setAncestor(String ancestor) {
                        this.ancestor = ancestor;
                    }

                    public String getAvatarUrl() {
                        return avatarUrl;
                    }

                    public void setAvatarUrl(String avatarUrl) {
                        this.avatarUrl = avatarUrl;
                    }

                    public boolean isCanDel() {
                        return canDel;
                    }

                    public void setCanDel(boolean canDel) {
                        this.canDel = canDel;
                    }

                    public String getComment() {
                        return comment;
                    }

                    public void setComment(String comment) {
                        this.comment = comment;
                    }

                    public String getCtime() {
                        return ctime;
                    }

                    public void setCtime(String ctime) {
                        this.ctime = ctime;
                    }

                    public String getDescendant() {
                        return descendant;
                    }

                    public void setDescendant(String descendant) {
                        this.descendant = descendant;
                    }

                    public String getDistance() {
                        return distance;
                    }

                    public void setDistance(String distance) {
                        this.distance = distance;
                    }

                    public String getId() {
                        return id;
                    }

                    public void setId(String id) {
                        this.id = id;
                    }

                    public String getIsDeleted() {
                        return isDeleted;
                    }

                    public void setIsDeleted(String isDeleted) {
                        this.isDeleted = isDeleted;
                    }

                    public String getIsResp() {
                        return isResp;
                    }

                    public void setIsResp(String isResp) {
                        this.isResp = isResp;
                    }

                    public String getIsShow() {
                        return isShow;
                    }

                    public void setIsShow(String isShow) {
                        this.isShow = isShow;
                    }

                    public String getMomentId() {
                        return momentId;
                    }

                    public void setMomentId(String momentId) {
                        this.momentId = momentId;
                    }

                    public String getNickName() {
                        return nickName;
                    }

                    public void setNickName(String nickName) {
                        this.nickName = nickName;
                    }

                    public String getOperator() {
                        return operator;
                    }

                    public void setOperator(String operator) {
                        this.operator = operator;
                    }

                    public String getOperatorId() {
                        return operatorId;
                    }

                    public void setOperatorId(String operatorId) {
                        this.operatorId = operatorId;
                    }

                    public int getPriseNum() {
                        return priseNum;
                    }

                    public void setPriseNum(int priseNum) {
                        this.priseNum = priseNum;
                    }

                    public String getRelationId() {
                        return relationId;
                    }

                    public void setRelationId(String relationId) {
                        this.relationId = relationId;
                    }

                    public String getRemark() {
                        return remark;
                    }

                    public void setRemark(String remark) {
                        this.remark = remark;
                    }

                    public String getRespAvatarUrl() {
                        return respAvatarUrl;
                    }

                    public void setRespAvatarUrl(String respAvatarUrl) {
                        this.respAvatarUrl = respAvatarUrl;
                    }

                    public String getRespCommentId() {
                        return respCommentId;
                    }

                    public void setRespCommentId(String respCommentId) {
                        this.respCommentId = respCommentId;
                    }

                    public String getRespNickName() {
                        return respNickName;
                    }

                    public void setRespNickName(String respNickName) {
                        this.respNickName = respNickName;
                    }

                    public String getUserId() {
                        return userId;
                    }

                    public void setUserId(String userId) {
                        this.userId = userId;
                    }

                    public String getUtime() {
                        return utime;
                    }

                    public void setUtime(String utime) {
                        this.utime = utime;
                    }
                }
            }
        }
    }
}
