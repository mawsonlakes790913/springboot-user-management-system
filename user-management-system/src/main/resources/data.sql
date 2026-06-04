INSERT INTO sample (id, str)
VALUES('1', 'Hello');

/* ユーザーマスタ */
INSERT INTO m_user (
    user_id
  , password
  , user_name
  , birthday
  , age
  , gender
  , department_id
  , role
) VALUES
  ('system@example.co.jp', 'password', 'システム管理者', '2000-01-01', 24, 1, 1, 'ROLE_ADMIN')
, ('user1@example.co.jp', 'password', 'ユーザー1', '2000-01-01', 24, 2, 2, 'ROLE_GENERAL')
, ('user2@example.co.jp', 'password', 'ユーザー2', '2000-01-01', 24, 2, 2, 'ROLE_GENERAL')
, ('user3@example.co.jp', 'password', 'ユーザー3', '2000-01-01', 24, 2, 2, 'ROLE_GENERAL')
, ('user4@example.co.jp', 'password', 'ユーザー4', '2000-01-01', 24, 2, 2, 'ROLE_GENERAL')
, ('user5@example.co.jp', 'password', 'ユーザー5', '2000-01-01', 24, 2, 2, 'ROLE_GENERAL')
, ('user6@example.co.jp', 'password', 'ユーザー6', '2000-01-01', 24, 2, 2, 'ROLE_GENERAL')
, ('user7@example.co.jp', 'password', 'ユーザー7', '2000-01-01', 24, 2, 2, 'ROLE_GENERAL')
, ('user8@example.co.jp', 'password', 'ユーザー8', '2000-01-01', 24, 2, 2, 'ROLE_GENERAL')
, ('user9@example.co.jp', 'password', 'ユーザー9', '2000-01-01', 24, 2, 2, 'ROLE_GENERAL')
, ('user10@example.co.jp', 'password', 'ユーザー10', '2000-01-01', 24, 2, 2, 'ROLE_GENERAL')
;

/* 部署マスタ */
INSERT INTO m_department (
    department_id
  , department_name
) VALUES
  (1, 'システム管理部')
, (2, '営業部')
;

/* 給料テーブル */
INSERT INTO t_salary (
    user_id
  , year_month
  , salary
) VALUES
  ('user1@example.co.jp', '2020/11', 280000)
, ('user1@example.co.jp', '2020/12', 290000)
, ('user1@example.co.jp', '2021/01', 300000)
;