--
-- PostgreSQL database dump
--

-- Dumped from database version 9.3.5
-- Dumped by pg_dump version 9.3.5
-- Started on 2014-10-24 14:11:21 CEST

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

--
-- TOC entry 178 (class 3079 OID 12018)
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- TOC entry 2239 (class 0 OID 0)
-- Dependencies: 178
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- TOC entry 175 (class 1259 OID 16419)
-- Name: clients; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE clients (
    id integer NOT NULL,
    name text NOT NULL
);


ALTER TABLE public.clients OWNER TO postgres;

--
-- TOC entry 173 (class 1259 OID 16415)
-- Name: clients_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE clients_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.clients_id_seq OWNER TO postgres;

--
-- TOC entry 2240 (class 0 OID 0)
-- Dependencies: 173
-- Name: clients_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE clients_id_seq OWNED BY clients.id;


--
-- TOC entry 174 (class 1259 OID 16417)
-- Name: clients_name_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE clients_name_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.clients_name_seq OWNER TO postgres;

--
-- TOC entry 2241 (class 0 OID 0)
-- Dependencies: 174
-- Name: clients_name_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE clients_name_seq OWNED BY clients.name;


--
-- TOC entry 172 (class 1259 OID 16405)
-- Name: messages; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE messages (
    id integer NOT NULL,
    sender text,
    reciever text,
    message text,
    "messageID" text,
    "timestamp" timestamp without time zone,
    "queueID" integer NOT NULL,
    "clientID" integer
);


ALTER TABLE public.messages OWNER TO postgres;

--
-- TOC entry 170 (class 1259 OID 16401)
-- Name: messages_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE messages_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.messages_id_seq OWNER TO postgres;

--
-- TOC entry 2242 (class 0 OID 0)
-- Dependencies: 170
-- Name: messages_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE messages_id_seq OWNED BY messages.id;


--
-- TOC entry 171 (class 1259 OID 16403)
-- Name: messages_queueID_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE "messages_queueID_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public."messages_queueID_seq" OWNER TO postgres;

--
-- TOC entry 2243 (class 0 OID 0)
-- Dependencies: 171
-- Name: messages_queueID_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE "messages_queueID_seq" OWNED BY messages."queueID";


--
-- TOC entry 177 (class 1259 OID 16432)
-- Name: queues; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE queues (
    id integer NOT NULL,
    name text,
    "queueID" text
);


ALTER TABLE public.queues OWNER TO postgres;

--
-- TOC entry 176 (class 1259 OID 16426)
-- Name: queues_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE queues_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.queues_id_seq OWNER TO postgres;

--
-- TOC entry 2244 (class 0 OID 0)
-- Dependencies: 176
-- Name: queues_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE queues_id_seq OWNED BY queues.id;


--
-- TOC entry 2110 (class 2604 OID 16422)
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY clients ALTER COLUMN id SET DEFAULT nextval('clients_id_seq'::regclass);


--
-- TOC entry 2111 (class 2604 OID 16439)
-- Name: name; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY clients ALTER COLUMN name SET DEFAULT nextval('clients_name_seq'::regclass);


--
-- TOC entry 2108 (class 2604 OID 16408)
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY messages ALTER COLUMN id SET DEFAULT nextval('messages_id_seq'::regclass);


--
-- TOC entry 2109 (class 2604 OID 16409)
-- Name: queueID; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY messages ALTER COLUMN "queueID" SET DEFAULT nextval('"messages_queueID_seq"'::regclass);


--
-- TOC entry 2112 (class 2604 OID 16435)
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY queues ALTER COLUMN id SET DEFAULT nextval('queues_id_seq'::regclass);


--
-- TOC entry 2229 (class 0 OID 16419)
-- Dependencies: 175
-- Data for Name: clients; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY clients (id, name) FROM stdin;
1	pedro
2	pablo
3	Anonymous
7	Perro
\.


--
-- TOC entry 2245 (class 0 OID 0)
-- Dependencies: 173
-- Name: clients_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('clients_id_seq', 7, true);


--
-- TOC entry 2246 (class 0 OID 0)
-- Dependencies: 174
-- Name: clients_name_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('clients_name_seq', 1, false);


--
-- TOC entry 2226 (class 0 OID 16405)
-- Dependencies: 172
-- Data for Name: messages; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY messages (id, sender, reciever, message, "messageID", "timestamp", "queueID", "clientID") FROM stdin;
1	kkka	kakak	kakaka	akakak	\N	111	\N
2				884dab65-fac7-4edd-8daa-297f0689357d	2014-10-21 23:49:57.628	-1	\N
\.


--
-- TOC entry 2247 (class 0 OID 0)
-- Dependencies: 170
-- Name: messages_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('messages_id_seq', 2, true);


--
-- TOC entry 2248 (class 0 OID 0)
-- Dependencies: 171
-- Name: messages_queueID_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('"messages_queueID_seq"', 1, false);


--
-- TOC entry 2231 (class 0 OID 16432)
-- Dependencies: 177
-- Data for Name: queues; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY queues (id, name, "queueID") FROM stdin;
1	perro	perro
2	perro	perro
3	perro	perro
11	general	24081d61-7ae8-4aba-a564-5c50114a3a93
12	general	4dd836ab-75c8-4ee2-8d4a-bd7b5bd4ac94
\.


--
-- TOC entry 2249 (class 0 OID 0)
-- Dependencies: 176
-- Name: queues_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('queues_id_seq', 12, true);


--
-- TOC entry 2116 (class 2606 OID 16425)
-- Name: clients_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY clients
    ADD CONSTRAINT clients_pkey PRIMARY KEY (id);


--
-- TOC entry 2114 (class 2606 OID 16414)
-- Name: messages_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY messages
    ADD CONSTRAINT messages_pkey PRIMARY KEY (id);


--
-- TOC entry 2238 (class 0 OID 0)
-- Dependencies: 6
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;
GRANT ALL ON SCHEMA public TO message WITH GRANT OPTION;


-- Completed on 2014-10-24 14:11:21 CEST

--
-- PostgreSQL database dump complete
--

