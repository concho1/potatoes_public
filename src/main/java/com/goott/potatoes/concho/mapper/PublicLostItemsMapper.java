package com.goott.potatoes.concho.mapper;

import com.goott.potatoes.concho.model.PublicLost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

@Mapper
public interface PublicLostItemsMapper {
    @Update("UPDATE public_lost_items SET date = #{dateTime}  WHERE id_plus = 'last_data_time'")
    void updateLastTimePortal(@Param("dateTime") Timestamp dateTime);
    @Select("SELECT date FROM public_lost_items WHERE id_plus = 'last_data_time'")
    Timestamp getLastTimePortal();
    @Update("UPDATE public_police_lost_items SET date = #{dateTime}  WHERE id_plus = 'last_data_time'")
    void updateLastTimePolice(@Param("dateTime") Timestamp dateTime);
    @Select("SELECT date FROM public_police_lost_items WHERE id_plus = 'last_data_time'")
    Timestamp getLastTimePolice();
    int insertPublicLostList(@Param("publicLostList") List<PublicLost> publicLostList);
    int insertPoliceLostList(@Param("publicLostList") List<PublicLost> publicLostList);
    List<PublicLost> getItemsByTelAndCategory(
        @Param("frontTel") String frontTel,
        @Param("majorCategory") String majorCategory,
        @Param("minorCategory") String minorCategory

    );
    List<PublicLost> getPoliceItemsByTelAndCategory(
        @Param("frontTel") String frontTel,
        @Param("majorCategory") String majorCategory,
        @Param("minorCategory") String minorCategory
    );
    List<PublicLost> getItemsBySearch(@Param("search") String search,@Param("limit") Integer limit,@Param("offset") Integer offset);
    List<PublicLost> getPoliceItemsBySearch(@Param("search") String search, @Param("limit") Integer limit,@Param("offset") Integer offset);
    List<PublicLost> getCombinedItemsBySearch(@Param("search") String search, @Param("limit") Integer limit,@Param("offset") Integer offset);

    int getItemsBySearchCnt(@Param("search") String search);
    int getPoliceItemsBySearchCnt(@Param("search") String search);
    int getCombinedItemsBySearchCnt(@Param("search") String search);
    PublicLost getItemByPlusId(@Param("idPlus") String idPlus);
    PublicLost getPoliceItemByPlusId(@Param("idPlus") String idPlus);
    boolean existsByDatePolice(@Param("date") LocalDate date);
    boolean existsByDatePortal(@Param("date") LocalDate date);
}
