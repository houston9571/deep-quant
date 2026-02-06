CREATE TABLE stock_quotation
(
    id            int         NOT NULL AUTO_INCREMENT,
    name          varchar(10) NOT NULL,
    symbol        varchar(10) NOT NULL,
    turnoverRate  decimal(16, 2) DEFAULT NULL,
    changeAmount  decimal(16, 2) DEFAULT NULL,
    changePercent decimal(16, 2) DEFAULT NULL,
    latestPrice   decimal(16, 2) DEFAULT NULL,
    highestPrice  decimal(16, 2) DEFAULT NULL,
    lowestPrice   decimal(16, 2) DEFAULT NULL,
    openPrice     decimal(16, 2) DEFAULT NULL,
    volume        int            DEFAULT NULL,
    turnover      decimal(16, 2) DEFAULT NULL,
    volumeRatio   decimal(16, 2) DEFAULT NULL,
    limitUp       decimal(16, 2) DEFAULT NULL,
    limitDown     decimal(16, 2) DEFAULT NULL,
    create_time   datetime       DEFAULT CURRENT_TIMESTAMP,
    update_time   datetime       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id) USING BTREE
) 