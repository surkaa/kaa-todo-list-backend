use user_center;
create table user_avatar
(
    id     bigint auto_increment comment '主键' primary key,
    avatar blob not null comment '二进制头像'
) comment '头像表';
create table if not exists user
(
    id            bigint auto_increment              not null comment '主键id',
    user_account  varchar(256)                       not null unique comment '登录账号',
    user_password varchar(512)                       not null comment '密码',
    username      varchar(256)                       null comment '用户昵称',
    avatar_id     bigint                             null comment '头像外键',
    gender        tinyint                            null comment '性别',
    phone         varchar(128)                       null comment '电话',
    email         varchar(512)                       null comment '邮箱',
    create_time   datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time   datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    user_status   tinyint  default 0                 not null comment '用户状态 0 - 正常',
    is_delete     tinyint  default 0                 not null comment '是否删除',
    primary key (id),
    foreign key (avatar_id) references user_avatar (id)
) comment '用户';