    package org.mbc.czo.function.cart.controller;

    import jakarta.validation.Valid;
    import lombok.RequiredArgsConstructor;
    import org.mbc.czo.function.cart.dto.CartOrderDTO;
    import org.mbc.czo.function.cart.dto.OrderDTO;
    import org.mbc.czo.function.cart.dto.OrderHistDTO;
    import org.mbc.czo.function.cart.service.CartService;
    import org.mbc.czo.function.cart.service.OrderService;
    import org.mbc.czo.function.member.domain.Member;
    import org.mbc.czo.function.member.repository.MemberJpaRepository;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.stereotype.Controller;
    import org.springframework.ui.Model;
    import org.springframework.web.bind.annotation.*;
    import jakarta.servlet.http.HttpSession;


    import java.security.Principal;
    import java.util.HashMap;
    import java.util.List;
    import java.util.Map;

    @Controller
    @RequiredArgsConstructor
    @RequestMapping("/cart/order")
    public class OrderController {

        private final CartService cartService;
        private final OrderService orderService;
        private final MemberJpaRepository memberJpaRepository;

        /* ============================
         * 단일 상품 바로 주문 (상세페이지)
         * POST /cart/order/single
         * ============================ */
        @PostMapping("/single")
        public @ResponseBody ResponseEntity<?> orderSingleItem(@RequestBody @Valid OrderDTO orderDTO,
                                                               Principal principal) {
            if (principal == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("redirect:/member/login");
            }

            String email = principal.getName();
            Long orderId = orderService.order(orderDTO, email);
            return new ResponseEntity<>(orderId, HttpStatus.OK);
        }

        /* ============================
         * 다중 상품 바로 주문
         * POST /cart/order/multi
         * ============================ */
        @PostMapping("/multi")
        public @ResponseBody ResponseEntity<?> orderMultiItems(@RequestBody @Valid List<OrderDTO> orderDTOList,
                                                               Principal principal) {
            if (principal == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("redirect:/member/login");
            }

            String email = principal.getName();
            Long orderId = orderService.orders(orderDTOList, email);
            return new ResponseEntity<>(orderId, HttpStatus.OK);
        }

        /* ============================
         * 장바구니 상품 주문 (선택 주문)
         * POST /cart/order/cart
         * ============================ */
        @PostMapping("/cart")
        public @ResponseBody ResponseEntity<?> orderCartItems(@RequestBody CartOrderDTO cartOrderDTO,
                                                              Principal principal) {
            if (principal == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("redirect:/member/login");
            }

            String email = principal.getName();
            List<CartOrderDTO> orderList = cartOrderDTO.getCartOrderDTOList();

            if (orderList == null || orderList.isEmpty()) {
                return new ResponseEntity<>("주문할 상품을 선택해주세요", HttpStatus.BAD_REQUEST);
            }

            for (CartOrderDTO order : orderList) {
                if (!cartService.isCartItemOwnedByUser(order.getCartItemId(), email)) {
                    return new ResponseEntity<>("주문 권한이 없습니다.", HttpStatus.FORBIDDEN);
                }
                if (order.getCount() <= 0 || order.getPrice() < 0) {
                    return new ResponseEntity<>("잘못된 상품 수량/가격입니다.", HttpStatus.BAD_REQUEST);
                }
            }

            Long orderId = cartService.orderCartItem(orderList, email);
            return new ResponseEntity<>(orderId, HttpStatus.OK);
        }

        /* ============================
         * 장바구니 전체 주문
         * POST /cart/order/all
         * ============================ */
        @PostMapping("/all")
        public String orderFromCart(Principal principal) {
            if (principal == null) {
                return "redirect:/member/login";
            }

            String email = principal.getName();
            orderService.orderFromCart(email);

            return "redirect:/cart/order/history";
        }

        /* ============================
         * 주문 내역 조회 (5개씩 페이징)
         * GET /cart/order/history
         * ============================ */
        @GetMapping("/history")
        public String showOrderHistory(Model model, Principal principal,
                                       @RequestParam(value = "page", defaultValue = "1") int page) {
            if (principal == null) {
                return "redirect:/member/login";
            }

            int pageSize = 5; // 한 페이지에 보여줄 주문 수
            List<OrderHistDTO> allOrders = cartService.getOrderHist(principal.getName());

            // 최근 주문이 먼저 오도록 정렬
            allOrders.sort((o1, o2) -> o2.getOrderDate().compareTo(o1.getOrderDate()));

            int totalOrders = allOrders.size();
            int totalPages = (int) Math.ceil((double) totalOrders / pageSize);

            int fromIndex = (page - 1) * pageSize;
            int toIndex = Math.min(fromIndex + pageSize, totalOrders);

            List<OrderHistDTO> orders = allOrders.subList(fromIndex, toIndex);

            Map<Long, Integer> orderTotalMap = new HashMap<>();
            for (OrderHistDTO order : orders) {
                int total = order.getOrderItems().stream()
                        .mapToInt(item -> item.getOrderPrice() * item.getCount())
                        .sum();
                orderTotalMap.put(order.getOrderId(), total);

                order.setMemberId(principal.getName());
            }

            model.addAttribute("orders", orders);
            model.addAttribute("orderTotalMap", orderTotalMap);
            model.addAttribute("page", page);
            model.addAttribute("totalPages", totalPages);

            return "order/orderHist";
        }

        /* ============================
         * 주문/결제 페이지
         * GET /cart/order
         * ============================ */
        @GetMapping("")
        public String showOrderPage(Model model, Principal principal) {
            if (principal == null) return "redirect:/member/login";

            String email = principal.getName();
            List<CartOrderDTO> cartItems = cartService.getCartOrdersForUser(email);

            int totalPrice = cartItems.stream()
                    .mapToInt(CartOrderDTO::getTotalPrice)
                    .sum();

            model.addAttribute("cartItems", cartItems);
            model.addAttribute("totalPrice", totalPrice);

            return "cart/order";
        }

        /* ============================
         * 주문 완료 처리
         * POST /cart/order/complete
         * ============================ */
        @PostMapping("/complete")
        public String completeOrder(@RequestParam("paymentMethod") String paymentMethod,
                                    Principal principal, Model model) {

            if (principal == null) {
                return "redirect:/member/login";
            }

            String email = principal.getName();

            Member member = memberJpaRepository.findByMemail(email)
                    .orElseThrow(() -> new IllegalStateException("회원 정보를 찾을 수 없습니다."));

            try {
                List<CartOrderDTO> cartOrders = cartService.getCartOrdersForUser(email);

                Long orderId = cartService.orderCartItem(cartOrders, email);

                int totalPrice = cartOrders.stream()
                        .mapToInt(CartOrderDTO::getTotalPrice)
                        .sum();

                model.addAttribute("orderId", orderId);
                model.addAttribute("totalPrice", totalPrice);

            } catch (Exception e) {
                e.printStackTrace();
                model.addAttribute("orderId", 999L);
                model.addAttribute("totalPrice", 0);
            }

            model.addAttribute("paymentMethod", paymentMethod);
            return "order/orderComplete";
        }

        /* 주문 취소 */
        @PostMapping("/cancel")
        @ResponseBody
        public String cancelOrder(@RequestParam Long orderId, Principal principal) {
            if (principal == null) {
                return "unauthorized"; // 로그인 안 된 경우
            }

            String email = principal.getName(); // 로그인된 사용자 이메일
            orderService.cancelOrder(orderId, email);

            return "success"; // 프론트에서 상태 업데이트용
        }

        @PostMapping("")
        @ResponseBody
        public ResponseEntity<?> orderCart(@RequestBody CartOrderDTO cartOrderDTO,
                                           Principal principal) {
            if (principal == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("redirect:/member/login");
            }

            String email = principal.getName();
            Long orderId = cartService.orderCartItem(cartOrderDTO.getCartOrderDTOList(), email);
            return new ResponseEntity<>(orderId, HttpStatus.OK);
        }




    }
