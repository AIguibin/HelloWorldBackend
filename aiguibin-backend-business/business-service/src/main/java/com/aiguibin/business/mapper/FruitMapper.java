package com.aiguibin.business.mapper;

import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;
@Mapper
@Repository
public interface FruitMapper {
    @Insert(value = "insert into season_fruit(fruitId,fruitName,production,appearance) values('1',苹果,河南省三门峡市灵宝县,圆形)")
    void addFruit();
    @Delete(value = "delete from season_fruit where fruitId='1'")
    void delFruit();
    @Update(value = "update season_fruit set production='河南省三门峡市灵宝县' where fruitId='1'")
    void megrFruit();
    @Select(value = "select * from season_fruit")
    List<?> findFruit();
}
