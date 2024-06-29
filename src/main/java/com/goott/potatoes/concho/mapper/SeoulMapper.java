package com.goott.potatoes.concho.mapper;

import com.goott.potatoes.concho.model.Seoul;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

@Mapper
public interface SeoulMapper {
    int insertSeoulList(@Param("seoulList") List<Seoul> seoulList);
    List<Seoul> findSeoulListByLimitAndOffset(@Param("limit") int limit, @Param("offset") int offset);
    List<Seoul> searchSeoulListByProductName(
            @Param("searchStr") String searchStr, @Param("day") int day,
            @Param("limit") int limit, @Param("offset") int offset);
    Integer searchSeoulListByProductNameCnt(
            @Param("searchStr") String searchStr, @Param("day") int day);

    List<Seoul> searchSeoulListByCategory(
            @Param("searchStr") String searchStr, @Param("day") int day,
            @Param("limit") int limit, @Param("offset") int offset);
    Integer searchSeoulListByCategoryCnt(
            @Param("searchStr") String searchStr, @Param("day") int day);
    List<Seoul> searchSeoulListByOrgName(
            @Param("searchStr") String searchStr, @Param("day") int day,
            @Param("limit") int limit, @Param("offset") int offset);
    Integer searchSeoulListByOrgNameCnt(
            @Param("searchStr") String searchStr, @Param("day") int day);
    List<Seoul> searchSeoulListBySearchStr(
            @Param("searchStr") String searchStr, @Param("day") int day,
            @Param("limit") int limit, @Param("offset") int offset);
    Integer searchSeoulListBySearchStrCnt(
            @Param("searchStr") String searchStr, @Param("day") int day);
    Seoul findSeoulById(@Param("actId") String actId);

    Seoul findSeoulByIdPlus(@Param("idPlus") String idPlus);

    boolean existsByDateSeoul(@Param("date") LocalDate date);

    @Update("UPDATE seoul SET date = #{dateTime}  WHERE act_id = 'last_data_time'")
    void updateLastTime(@Param("dateTime") Timestamp dateTime);
    @Select("SELECT date FROM seoul WHERE act_id = 'last_data_time'")
    Timestamp getLastTime();

    @Select("SELECT NOW()")
    Timestamp getNowTime();
    List<Seoul> getSeoulListByCate(@Param("major") String major, @Param("minor") String minor);

    List<Seoul> getSeoulListBySearch(@Param("search") String search,@Param("limit") Integer limit,@Param("offset") Integer offset);

    int getSeoulListBySearchCnt(@Param("search") String search);
}
