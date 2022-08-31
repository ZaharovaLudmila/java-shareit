CREATE TABLE IF NOT EXISTS USERS (
    USER_ID BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    USER_NAME VARCHAR(300) NOT NULL,
    EMAIL VARCHAR(320) NOT NULL,
    CONSTRAINT PK_USER PRIMARY KEY (USER_ID),
    CONSTRAINT UQ_USER_EMAIL UNIQUE (EMAIL)
);

CREATE TABLE IF NOT EXISTS REQUESTS (
    REQUEST_ID BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    DESCRIPTION VARCHAR(500) NOT NULL,
    REQUESTER_ID BIGINT REFERENCES USERS (USER_ID) ON DELETE CASCADE,
    CREATED_DATE TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT PK_REQUEST PRIMARY KEY (REQUEST_ID)
);

CREATE TABLE IF NOT EXISTS ITEMS (
    ITEM_ID BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    ITEM_NAME VARCHAR(300) NOT NULL,
    DESCRIPTION VARCHAR(500) NOT NULL,
    IS_AVAILABLE BOOLEAN NOT NULL,
    OWNER_ID BIGINT REFERENCES USERS (USER_ID) ON DELETE CASCADE,
    REQUEST_ID BIGINT REFERENCES REQUESTS (REQUEST_ID) ON DELETE CASCADE,
    CONSTRAINT PK_ITEM PRIMARY KEY (ITEM_ID)
);

CREATE TABLE IF NOT EXISTS BOOKINGS (
    BOOKING_ID BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    START_DATE TIMESTAMP WITHOUT TIME ZONE,
    END_DATE TIMESTAMP WITHOUT TIME ZONE,
    ITEM_ID BIGINT REFERENCES ITEMS (ITEM_ID) ON DELETE CASCADE,
    BOOKER_ID BIGINT REFERENCES USERS (USER_ID) ON DELETE CASCADE,
    STATUS varchar(50),
    CONSTRAINT PK_BOOKING PRIMARY KEY (BOOKING_ID)
);

CREATE TABLE IF NOT EXISTS COMMENTS (
    COMMENT_ID BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    TEXT VARCHAR(500) NOT NULL,
    ITEM_ID BIGINT REFERENCES ITEMS (ITEM_ID) ON DELETE CASCADE,
    AUTHOR_ID BIGINT REFERENCES USERS (USER_ID) ON DELETE CASCADE,
    CREATED TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT PK_COMMENT PRIMARY KEY (COMMENT_ID)
);


