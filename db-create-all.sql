create table article (
  id                            bigint auto_increment not null,
  title                         varchar(255),
  input_content                 TEXT,
  output_content                TEXT,
  format_type                   integer,
  author_id                     bigint not null,
  version                       bigint not null,
  when_created                  datetime(6) not null,
  when_modified                 datetime(6) not null,
  deleted                       tinyint(1) default 0 not null,
  constraint ck_article_format_type check (format_type in (0,1,2)),
  constraint pk_article primary key (id)
);

create table comment (
  id                            bigint auto_increment not null,
  content                       TEXT,
  author_id                     bigint not null,
  target_type                   integer,
  target_id                     bigint,
  version                       bigint not null,
  when_created                  datetime(6) not null,
  when_modified                 datetime(6) not null,
  deleted                       tinyint(1) default 0 not null,
  constraint ck_comment_target_type check (target_type in (0,1)),
  constraint pk_comment primary key (id)
);

create table login_pass (
  pass_id                       varchar(255) not null,
  user_id                       bigint not null,
  when_to_expire                datetime(6),
  version                       bigint not null,
  when_created                  datetime(6) not null,
  when_modified                 datetime(6) not null,
  constraint pk_login_pass primary key (pass_id)
);

create table user (
  id                            bigint auto_increment not null,
  email                         varchar(255),
  password                      varchar(255),
  name                          varchar(255),
  avatar                        varchar(255),
  intro                         varchar(255),
  version                       bigint not null,
  when_created                  datetime(6) not null,
  when_modified                 datetime(6) not null,
  deleted                       tinyint(1) default 0 not null,
  constraint pk_user primary key (id)
);

alter table article add constraint fk_article_author_id foreign key (author_id) references user (id) on delete restrict on update restrict;
create index ix_article_author_id on article (author_id);

alter table comment add constraint fk_comment_author_id foreign key (author_id) references user (id) on delete restrict on update restrict;
create index ix_comment_author_id on comment (author_id);

