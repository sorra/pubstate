-- apply changes
create table article (
  id                            char(22) not null,
  author_id                     char(22) not null,
  title                         varchar(255) not null,
  input_content                 TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci not null,
  output_content                TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci not null,
  format_type                   integer not null,
  version                       bigint not null,
  when_created                  datetime(6) not null,
  when_modified                 datetime(6) not null,
  deleted                       tinyint(1) default 0 not null,
  constraint pk_article primary key (id)
);

create table comment (
  id                            char(22) not null,
  author_id                     char(22) not null,
  content                       TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci not null,
  target_type                   integer not null,
  target_id                     varchar(255) not null,
  version                       bigint not null,
  when_created                  datetime(6) not null,
  when_modified                 datetime(6) not null,
  deleted                       tinyint(1) default 0 not null,
  constraint pk_comment primary key (id)
);

create table draft (
  id                            char(22) not null,
  author_id                     char(22) not null,
  target_id                     varchar(255) not null,
  title                         varchar(255) not null,
  input_content                 TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci not null,
  format_type                   integer not null,
  version                       bigint not null,
  when_created                  datetime(6) not null,
  when_modified                 datetime(6) not null,
  constraint pk_draft primary key (id)
);

create table image (
  id                            char(22) not null,
  owner_id                      char(22) not null,
  is_avatar                     tinyint(1) default 0 not null,
  version                       bigint not null,
  when_created                  datetime(6) not null,
  when_modified                 datetime(6) not null,
  deleted                       tinyint(1) default 0 not null,
  constraint pk_image primary key (id)
);

create table login_pass (
  pass_id                       varchar(255) not null,
  user_id                       varchar(255) not null,
  when_to_expire                datetime(6) not null,
  version                       bigint not null,
  when_created                  datetime(6) not null,
  when_modified                 datetime(6) not null,
  constraint pk_login_pass primary key (pass_id)
);

create table user (
  id                            char(22) not null,
  email                         varchar(255) not null,
  password                      varchar(255) not null,
  name                          varchar(255) not null,
  avatar                        varchar(255) not null,
  intro                         varchar(255) not null,
  version                       bigint not null,
  when_created                  datetime(6) not null,
  when_modified                 datetime(6) not null,
  deleted                       tinyint(1) default 0 not null,
  constraint uq_user_email unique (email),
  constraint pk_user primary key (id)
);

create index ix_article_author_id on article (author_id);
alter table article add constraint fk_article_author_id foreign key (author_id) references user (id) on delete restrict on update restrict;

create index ix_comment_author_id on comment (author_id);
alter table comment add constraint fk_comment_author_id foreign key (author_id) references user (id) on delete restrict on update restrict;

create index ix_draft_author_id on draft (author_id);
alter table draft add constraint fk_draft_author_id foreign key (author_id) references user (id) on delete restrict on update restrict;

create index ix_image_owner_id on image (owner_id);
alter table image add constraint fk_image_owner_id foreign key (owner_id) references user (id) on delete restrict on update restrict;

