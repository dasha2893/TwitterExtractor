CREATE TABLE sentiment_positive
(
  id serial NOT NULL,
  text character(4000),
  type character(500),
  CONSTRAINT sentiment_pkey PRIMARY KEY (id)
)
;

CREATE TABLE sentiment_negative
(
  id serial NOT NULL,
  text character(4000),
  type character(500),
  CONSTRAINT sentiment_neg_pkey PRIMARY KEY (id)
)
;

CREATE TABLE accounts
(
  consumer_key character(250),
  consumer_secret character(250),
  access_token character(250),
  token_secret character(250)
)
;

INSERT INTO accounts VALUES
  ('FE3oc3mDHf945MpCn77QC6TOO','L7H9FsWqY8ATMJPoRrjbTUbTVr49uaXMSklvhd3C8Q6cW4jFgo','697749814147207171-uxaBONI0t95pJAhza6XOgyjHFNVpIg3','SNsfIVo5pMXdF3sS1UQ4gtTLQrjEmuYEAeHmrLkEOdUNY'),
  ('OiYqT0S40nfBijxhVDuHuecfu','39YbBC9khwXMfhkRi9EKosnOYmcM4szRHt3c7lCX0UFjci0x9V','697749814147207171-vJ4wWC1tqXhJ6ZKaMyxY88BXFnXkNHm','1lkzXjjLKwq8pO8GfOqFXa1Nn2i16aKdcSNjvSVCAsZWu'),
  ('MIu9M6BL0ye7Uj5Ajj9BYCrJ7','OWn7aqRBVaHEkh3Vq56BUOvmwvl9gfYcFuXbg0MrK1JMWePhlu','697749814147207171-f5qe8d6LbGCeiSyxFwfUP6ZiZGs8Wnk','1sAmdhJrHEMqiPY0xBtfFSIdbbk4wwTNYQESKLJFdp1bu'),
  ('m5Q8k3hILZ2BD0oi8XQpQCcJ2','hvDE4vSFmYTgrqrNHdoyRLlR3aFwzNQTtHburkwhPQmZcV65a4','697749814147207171-5I3jLoofhbg75AW6AtrZvp9T5ooUe5c','FWk3FmlKWFPigq7skq0N9f2DpJWf4c32EVpPYoBdegV3K')
;

--после заполнения таблиц обязательно выполнить эти скрипты
update sentiment_positive
set text=text||type, type='1'
where type!='1';

update sentiment_negative
set text=text||type, type='-1'
where type!='-1';

--------------------------------------------------------------

create table emojis
(
  id serial NOT NULL,
  tweetAndMark character(20),
  markOfEmojis int
);


CREATE TABLE dictionary_Ling
(
  id serial NOT NULL,
  JSONWord character(400) UNIQUE
);

CREATE TABLE dictionary_tweet
(
  id serial NOT NULL,
  JSONWord character(400) UNIQUE
);


CREATE TABLE words_for_removal
(
  id serial NOT NULL,
  JSONWord character(400) UNIQUE,
  part_of_speech character(30)

);


create TABLE dictionary
(
  id serial NOT NULL,
  JSONWord CHARACTER(100),
  count_in_positive int,
  count_in_negative int,
  count_in_neutral int

);

CREATE table frequency_table
(
  count_negative_tweet int,
  count_positive_tweet int,
  count_neutral_tweet int,
  count_positive_words int,
  count_negative_words int,
  count_neutral_words int
);

insert into frequency_table VALUES (0,0,0,0,0,0);
-------------------------------------------------------------------
create table tweet (
  id serial NOT NULL,
  tweet character(4000),
  markOfEmojis int,
  isMark int
);


create or REPLACE FUNCTION deleteEmoji(tweet in VARCHAR(4000)) RETURNS VARCHAR(4000)
AS $$
DECLARE
  text         VARCHAR(4000) := tweet;
  markOfEmojis integer;
  sum          integer :=0;
  i            emoticons%ROWTYPE;
BEGIN
  FOR i IN select * from emoticons LOOP
    IF (text  like '%' || i.emoticon || '%') THEN text:=replace(text, i.emoticon, ''); sum := sum + i.mark;
    END IF;
  END LOOP;
  RETURN text || ' sum_of_mark=' || sum;
END;
$$ LANGUAGE plpgsql;


create or REPLACE FUNCTION deleteUnionsPronounsPrepositions(tweet in VARCHAR(4000)) RETURNS VARCHAR(4000)
AS $$
DECLARE
  text VARCHAR(4000) := tweet;
  i words_for_removal%ROWTYPE;
BEGIN
  FOR i IN select * from words_for_removal LOOP
    select regexp_replace(text,'^('||i.JSONWord||')(\s+.*)','\2') into text;
    select regexp_replace(text,'(.*\s+)('||i.JSONWord||')$','\1') into text;
    select regexp_replace(text,'\s+('||i.JSONWord||')\s',' ','g')  into text;
    select regexp_replace(text,'\sне\s('||i.JSONWord||')\s',' ','g')  into text;
  END LOOP;
  RETURN text;
END;
$$ LANGUAGE plpgsql;


----------для тестов
CREATE TABLE pos_neg_obuch
(
  id serial NOT NULL,
  text character(4000),
  type character(500)
)
;

CREATE TABLE posts
(
  id serial NOT NULL,
  text character(4000),
  type character(10),
  date BIGINT
)
;

CREATE TABLE pos_neg_test
(
  id serial NOT NULL,
  text character(4000),
  type_obuch CHARACTER(5),
  type_test CHARACTER(5),
  isMarked int DEFAULT 0,
  date CHARACTER(20)
)
;
--update pos_neg_test set date=to_char((now() - interval '1 day'), 'YYYY-MM-DD');
--to_char((now() - interval '1 day'), 'YYYY-MM-DD')

insert into pos_neg_obuch (text, type)
  select text,type from
    (select p.*, row_number() OVER (order BY id) rn
     from sentiment_positive p)t
  where t.rn<=58000
  union all
  select text,type from
    (select n.*, row_number() OVER (order by id) rn
     from sentiment_negative n)t2
  where t2.rn<=56000
;

insert into pos_neg_test (text, type)
  select text,type from
    (select p.*, row_number() OVER (order BY id) rn
     from sentiment_positive p)t
  where t.rn>58000
  union all
  select text,type from
    (select n.*, row_number() OVER (order by id) rn
     from sentiment_negative n)t2
  where t2.rn>56000
;



create or REPLACE FUNCTION addDataToDictionary (w in VARCHAR(100), m in INT)
  RETURNS INT
AS $$
DECLARE
  id_word int :=0;
  value int :=0;
  count_positive int :=0;
  count_negative int :=0;
  count_neutral int :=0;
BEGIN
  CASE
    WHEN m=1 THEN count_positive:=1;
    WHEN m=0 THEN count_neutral:=1;
    WHEN m=-1 THEN count_negative:=1;
  END CASE;
  select id from dictionary where JSONWord=w into id_word;
  IF id_word is null THEN
    INSERT INTO dictionary (JSONWord,count_in_positive,count_in_negative,count_in_neutral)
    VALUES (w, count_positive, count_negative, count_neutral);
    RETURN 1;
  END IF;
  IF id_word is not null THEN
    update dictionary SET count_in_positive = count_in_positive + count_positive,
      count_in_negative = count_in_negative + count_negative,
      count_in_neutral = count_in_neutral + count_neutral
    where id = id_word;
    RETURN 2;
  END IF;
END;
$$ LANGUAGE plpgsql;


CREATE table temporary_table_words(
  w CHAR(100),
  c INTEGER
);

CREATE OR REPLACE FUNCTION getCountWordInSelection(IN array_words CHAR(100)[], IN type_selection int, OUT w CHAR(100), OUT c INTEGER)
  RETURNS SETOF RECORD AS
$$
DECLARE
  l INTEGER := array_length(array_words,1);
  v_word dictionary.JSONWord%type;
  v_count int;
BEGIN
  IF (type_selection=1) THEN
    for i in 1..l loop
      SELECT JSONWord, count_in_positive into v_word, v_count FROM dictionary where JSONWord=array_words[i];
      IF v_word is null THEN RETURN QUERY select array_words[i] , 0;
      ELSE RETURN QUERY SELECT v_word, v_count;
      END IF;
    END LOOP;
  END IF;
  IF (type_selection=-1) THEN
    for i in 1..l loop
      SELECT JSONWord, count_in_negative into v_word, v_count  FROM dictionary where JSONWord=array_words[i];
      IF v_word is null THEN RETURN QUERY select array_words[i] , 0;
      ELSE RETURN QUERY SELECT v_word, v_count;
      END IF;
    END LOOP;
  END IF;
END;
$$
LANGUAGE plpgsql;


str= RT  Травмировала  Ну а че?Она думала меня напугать?Наивная. Надо же мне было показать ей вещи куда хуже :D
mark1 = 1
tweet1= RT  Травмировала  Ну а че?Она думала меня напугать?Наивная. Надо же мне было показать ей вещи куда хуже
1= RT  Травмировала  Ну а че?Она думала меня напугать?Наивная. Надо же мне было показать ей вещи куда хуже
2= травмировала че думала напугать наивная было показать вещи куда хуже  ST= false
10
