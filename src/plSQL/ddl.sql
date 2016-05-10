DROP TABLE seat;
DROP TABLE airplane;

CREATE TABLE airplane (
    plane_no VARCHAR2(10),
    model VARCHAR2(30),
    seats NUMBER(*,0),
    CONSTRAINT airplane_pk PRIMARY KEY (plane_no) 
);

CREATE TABLE seat (
    PLANE_NO VARCHAR2(10),
    SEAT_NO VARCHAR2(4),
    RESERVED NUMBER UNIQUE,
    BOOKED NUMBER,
    BOOKING_TIME NUMBER,
    CONSTRAINT seat_pk PRIMARY KEY (plane_no, seat_no),
    CONSTRAINT seat_fk FOREIGN KEY (plane_no) REFERENCES airplane 
);

INSERT INTO airplane VALUES('CR9', 'SAS Airlines Canadair CRJ-900', 96);

BEGIN
FOR x IN 1..24 LOOP
    INSERT INTO seat VALUES ('CR9', 'A'||x, NULL, NULL, NULL);
    INSERT INTO seat VALUES ('CR9', 'C'||x, NULL, NULL, NULL);
    INSERT INTO seat VALUES ('CR9', 'D'||x, NULL, NULL, NULL);
    INSERT INTO seat VALUES ('CR9', 'F'||x, NULL, NULL, NULL);
END LOOP;
END;

COMMIT;
