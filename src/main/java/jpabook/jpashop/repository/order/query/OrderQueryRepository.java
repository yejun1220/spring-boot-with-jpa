package jpabook.jpashop.repository.order.query;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class OrderQueryRepository {

    private final EntityManager em;

    public List<OrderQueryDto> findOrderDtoList() {
        List<OrderQueryDto> orderQueryDtoList = findOrders();
        orderQueryDtoList.forEach(o -> {
            // orderId로 여러개의 orderItem을 가져온 후 List에 저장한다.
            List<OrderItemQueryDto> orderItemList = findOrderItems(o.getOrderId());
            o.setOrderItems(orderItemList);
        });

        return orderQueryDtoList;
    }

    private List<OrderItemQueryDto> findOrderItems(Long orderId) {
        return em.createQuery("select new jpabook.jpashop.repository.order.query" +
                        " .OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count)" +
                        " from OrderItem oi" +
                        " join oi.item i" +
                        " where oi.order.id = :orderId", OrderItemQueryDto.class)
                .setParameter("orderId", orderId)
                .getResultList();
    }

    private List<OrderQueryDto> findOrders() {
        return em.createQuery("select new jpabook.jpashop.repository.order.query" +
                        " .OrderQueryDto(o.id, m.name, o.orderDate, o.status, d.address)" +
                        " from Order o" +
                        " join o.member m" +
                        " join o.delivery d", OrderQueryDto.class)
                .getResultList();
    }

    public List<OrderQueryDto> findAllByDto_optimization() {
        List<OrderQueryDto> orderQueryDtoList = findOrders();

        List<Long> orderIdList = orderQueryDtoList.stream().map(o -> o.getOrderId()).collect(Collectors.toList());

        List<OrderItemQueryDto> orderItemList = em.createQuery("select new jpabook.jpashop.repository.order.query" +
                " .OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count)" +
                " from OrderItem oi" +
                " join oi.item i" +
                " where oi.order.id in :orderIdList", OrderItemQueryDto.class)
                .setParameter("orderIdList", orderIdList)
                .getResultList();

        Map<Long, List<OrderItemQueryDto>> orderItemMap = orderItemList.stream().collect(Collectors.groupingBy(orderItemQueryDto -> orderItemQueryDto.getOrderId()));

        orderQueryDtoList.forEach(o -> o.setOrderItems(orderItemMap.get(o.getOrderId())));

        return orderQueryDtoList;

    }
}
