package org.mbc.czo.function.boardAdmin.Search;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPQLQuery;
import lombok.RequiredArgsConstructor;
import org.mbc.czo.function.boardAdmin.domain.BoardAdmin;
import org.mbc.czo.function.boardAdmin.domain.QBoardAdmin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Service;

import java.util.List;

@Service

public class BoardAdminSearchImpl extends QuerydslRepositorySupport implements BoardAdminSearch { // 쿼리dsl레포지토리 상속받고 BoardAdminSearch 임폴트 받음
        // 구현 클래스
        //                               상속                              임폴트


        public BoardAdminSearchImpl() {  // 위에 필드 클릭하면 자동 생성
            super(BoardAdmin.class);
        }

        @Override
        public Page<BoardAdmin> search1(Pageable pageable) { // 위에 필드 클릭하면 자동 생성

            QBoardAdmin boardAdmin = QBoardAdmin.boardAdmin;  // Q도메인 객체
            JPQLQuery<BoardAdmin> query = from(boardAdmin);
            query.where(boardAdmin.title.contains("1"));
            List<BoardAdmin> list = query.fetch();
            long count = query.fetchCount();

            return new PageImpl<>(list, pageable, count);
        }

    @Override
    public Page<BoardAdmin> searchAll1(String[] types, String keyword, Pageable pageable) {  // 인터페이스에서 자동 생성

            QBoardAdmin boardAdmin = QBoardAdmin.boardAdmin;
            JPQLQuery<BoardAdmin> query = from(boardAdmin);

            if(types != null && types.length > 0 && keyword != null) {  // 검색 키워드와 조건이 있으면

                BooleanBuilder booleanBuilder = new BooleanBuilder(); //(
                for(String type: types) {
                    switch (type){
                        case "t": // 제목 입력시
                            booleanBuilder.or(boardAdmin.title.contains(keyword));
                            break;
                        case"c": // 내용 입력시
                            booleanBuilder.or(boardAdmin.content.contains(keyword));
                            break;
                        case "w":  // 작성자 입력시
                            booleanBuilder.or(boardAdmin.writer.contains(keyword));
                            break;
                    }  // 스위치 종료
                }  // 폴 종료
                query.where(booleanBuilder);
            }  // 엔드 이프 종료

        query.where(boardAdmin.bno.gt(0L));
            this.getQuerydsl().applyPagination(pageable, query);
            List<BoardAdmin> list = query.fetch();
            long count = query.fetchCount();
        return new PageImpl<>(list, pageable, count);
    }


}
