alter table article drop foreign key fk_article_author_id;
drop index ix_article_author_id on article;

alter table comment drop foreign key fk_comment_author_id;
drop index ix_comment_author_id on comment;

alter table draft drop foreign key fk_draft_author_id;
drop index ix_draft_author_id on draft;

drop table if exists article;

drop table if exists comment;

drop table if exists draft;

drop table if exists file_item;

drop table if exists login_pass;

drop table if exists user;

