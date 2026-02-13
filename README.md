
Deep Think Quant System

1. 股票基本信息,所属概念


2. 股票实时交易行情,实时资金流向


3. 龙虎榜信息，主力买入





#######################
1. 收盘获取所有股票当天最新数据 
   9:31 11:31 15:01   127.0.0.1/task/stock/delay -- stock_delay
2. 收盘后获取龙虎榜数据 
   16:00  127.0.0.1/task/dragon/2026-02-10  -- stock_dragon stock_dragon_detail
3. 板块列表 
   点击后可实时更新  收盘后自动更新一次 127.0.0.1/task/board/delay -- board_delay 
4. 通过所属概念的更新个股列表
   板块更新后，再更新板块所属的股票 stock_board  stock_delay