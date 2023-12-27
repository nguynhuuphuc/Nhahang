package com.example.nhahang.Interfaces;

import com.example.nhahang.Models.AccountModel;
import com.example.nhahang.Models.ConversationModel;
import com.example.nhahang.Models.CustomerModel;
import com.example.nhahang.Models.LocationModel;
import com.example.nhahang.Models.MessageModel;
import com.example.nhahang.Models.NotificationModel;
import com.example.nhahang.Models.OrderItemModel;
import com.example.nhahang.Models.OrderItemsHisModel;
import com.example.nhahang.Models.OrderKitchenModel;
import com.example.nhahang.Models.OrderModel;
import com.example.nhahang.Models.PaidOrderModel;
import com.example.nhahang.Models.PaymentMethodModel;
import com.example.nhahang.Models.Requests.CheckEmailRequest;
import com.example.nhahang.Models.Employee;
import com.example.nhahang.Models.Requests.CreatePasswordRequest;
import com.example.nhahang.Models.Requests.JoinOrderTablesRequest;
import com.example.nhahang.Models.Requests.LoginRequest;
import com.example.nhahang.Models.Requests.NotificationRequest;
import com.example.nhahang.Models.Requests.OrderKitchenRequest;
import com.example.nhahang.Models.Requests.OrderRequest;
import com.example.nhahang.Models.Requests.PhoneNumberRequest;
import com.example.nhahang.Models.Requests.PositionIdRequest;
import com.example.nhahang.Models.Requests.RevenueRequest;
import com.example.nhahang.Models.Requests.ServerRequest;
import com.example.nhahang.Models.ReservationModel;
import com.example.nhahang.Models.Respones.ChangeOrderTableResponse;
import com.example.nhahang.Models.Respones.LoginResponse;
import com.example.nhahang.Models.Respones.RevenueResponse;
import com.example.nhahang.Models.Respones.ServerResponse;
import com.example.nhahang.Models.Requests.UserUidRequest;
import com.example.nhahang.Models.TableModel;
import com.example.nhahang.Utils.Util;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Array;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {

    Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .create();

    ApiService apiService = new Retrofit.Builder()
            .baseUrl("http://"+ Util.localhost+ ":3000/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ApiService.class);

    @POST("login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @POST("login/login_kitchen")
    Call<LoginResponse> loginKitchen(@Body LoginRequest loginRequest);

    @POST("user/employee")
    Call<Employee> getEmployee(@Body UserUidRequest userUidRequest);

    @POST("user/get_info_customer")
    Call<CustomerModel> getCustomer(@Body UserUidRequest userUidRequest);

    @POST("user/delete_customer")
    Call<ServerResponse> deleteCustomer(@Body UserUidRequest userUidRequest);

    @POST("user/update_employee")
    Call<Employee> updateEmployee(@Body Employee employee);

    @POST("user/update_customer")
    Call<CustomerModel> updateCustomer(@Body CustomerModel customerModel);

    @POST("user/add_new_employee")
    Call<ServerResponse> addNewEmployee(@Body Employee employee);

    @POST("user/email_is_exists")
    Call<ServerResponse> checkEmailExists(@Body CheckEmailRequest checkEmailRequest);


    @POST("user/phone_number_is_exists")
    Call<ServerResponse> checkPhoneNumberRequest(@Body PhoneNumberRequest phoneNumberRequest);

    @POST("user/get_list_employees_byPositionId")
    Call<ArrayList<Employee>> getListEmployeesByPositionId(@Body PositionIdRequest positionIdRequest);

    @POST("user/get_customers")
    Call<ArrayList<CustomerModel>> getCustomers();

    @POST("user/delete_employee")
    Call<Employee> deleteEmployee(@Body UserUidRequest request);

    @POST("user/get_employee_by_id")
    Call<Employee> getEmployeeById(@Body UserUidRequest request);

    @POST("account/check_password_exists")
    Call<ServerResponse> checkPasswordExists(@Body PhoneNumberRequest phoneNumberRequest);



    @POST("account/add_new_account")
    Call<ServerResponse> addNewAccount(@Body AccountModel accountModel);

    @POST("account/create_password")
    Call<ServerResponse> createPassword(@Body CreatePasswordRequest passwordRequest);

    @POST("home/get_all_tables")
    Call<ArrayList<TableModel>> getAllTables(@Body UserUidRequest userUidRequest);


    @POST("home/get_table_by_id")
    Call<TableModel> getTableById(@Body TableModel tableModel);

    @POST("home/get_all_locations")
    Call<ArrayList<LocationModel>> getAllLocations(@Body UserUidRequest userUidRequest);

    @GET("home/get_conservations")
    Call<ArrayList<ConversationModel>> getConversations();

    @POST("home/get_conservation")
    Call<ArrayList<MessageModel>> getMessages(@Body ServerRequest request);

    @POST("home/read_messages")
    Call<ServerResponse> readMessages(@Body ServerRequest request);

    @POST("order/add_new_order")
    Call<TableModel> addNewOrder(@Body OrderRequest orderRequest);

    @POST("order/get_order_by_table_id")
    Call<OrderModel> getOrderByTableId(@Body OrderRequest orderRequest);

    @POST("order/get_order_by_id")
    Call<OrderModel> getOrderById(@Body OrderRequest orderRequest);

    @POST("order/get_order_items")
    Call<ArrayList<OrderItemModel>> getOrderItems(@Body OrderRequest orderRequest);

    @POST("order/update_order_items")
    Call<ServerResponse> updateOrderItems(@Body OrderRequest orderRequest);

    @POST("order/update_orders")
    Call<ServerResponse> updateOrders(@Body OrderRequest orderRequest);

    @GET("order/get_all_orders")
    Call<ArrayList<OrderModel>> getAllOrders();

    @POST("order/get_order_items_history")
    Call<ArrayList<OrderItemsHisModel>> getOrderItemsHisByOrderId(@Body OrderRequest orderRequest);

    @POST("order/update_paid_order")
    Call<ServerResponse> updatePaidOrder(@Body OrderRequest orderRequest);

    @POST("order/update_change_table")
    Call<ArrayList<TableModel>> changeOrderTable(@Body OrderRequest orderRequest);

    @POST("order/join_order_table")
    Call<ArrayList<TableModel>> joinOrderTables(@Body JoinOrderTablesRequest request);

    @POST("order/split_order_table")
    Call<ArrayList<TableModel>> splitOrderTable(@Body OrderRequest request);

    @POST("order/checker/is_table_occupied")
    Call<ServerResponse> orderCheckTableOccupied(@Body OrderRequest orderRequest);



    @POST("payment/exchange")
    Call<String> paymentExchange(@Body OrderRequest orderRequest);

    @POST("payment/create_payment_url")
    Call<String> paymentVNPAY(@Body OrderRequest orderRequest);

    @GET("payment/get_all_payment_method")
    Call<ArrayList<PaymentMethodModel>> getAllPaymentMethod();

    @POST("history/get_paid_orderitems")
    Call<ArrayList<OrderItemModel>> getPaidOrderItems(@Body OrderRequest request);

    @GET("history/get_all_paid_orders")
    Call<ArrayList<PaidOrderModel>> getAllPaidOrders();

    @POST("history/get_revenue")
    Call<ArrayList<RevenueResponse>> getRevenue(@Body RevenueRequest request);

    @POST("kitchen/serv_order_items")
    Call<ServerResponse> servOrderItems(@Body OrderRequest request);

    @POST("kitchen/serv_order_items_check_list")
    Call<ArrayList<OrderItemModel>> servOrderItemsCheckList(@Body OrderRequest request);

    @POST("kitchen/checker/is_serv_order")
    Call<ServerResponse> isServOrder(@Body OrderRequest request);

    @POST("kitchen/confirm_order_items")
    Call<ServerResponse> confirmOrderItems(@Body OrderRequest request);

    @POST("kitchen/confirm_order_items_check_list")
    Call<ArrayList<OrderItemModel>> confirmOrderItemsCheckList(@Body OrderRequest request);

    @POST("kitchen/checker/is_confirm_order")
    Call<ServerResponse> isConfirmOrder(@Body OrderRequest request);

    @GET("kitchen/get_order_check_list")
    Call<ArrayList<OrderKitchenModel>> getCheckListOrder();

    @POST("kitchen/delete_order")
    Call<ServerResponse> kitchenDeleteOrder(@Body ServerRequest request);

    @POST("kitchen/delete_order_item")
    Call<ServerResponse> kitchenDeleteOrderItem(@Body ServerRequest request);


    @POST("kitchen/get_order_items")
    Call<ArrayList<OrderItemModel>> getOrderItemKitchen(@Body OrderKitchenRequest request);

    @POST("kitchen/change_order_item_quantity")
    Call<ServerResponse> kitchenChangeOrderItemQuantity(@Body ServerRequest request);

    @GET("notification/get_notifications")
    Call<ArrayList<NotificationModel>> getNotifications();

    @POST("notification/get_notifications")
    Call<ArrayList<NotificationModel>> getNotifications(@Body NotificationRequest request);

    @GET("notification/get_notify_count")
    Call<NotificationModel> getNotificationsCount();

    @POST("booking/get_reservations_by_day2")
    Call<ArrayList<ReservationModel>> getReservationsByDay(@Body ServerRequest serverRequest);

    @POST("booking/update_reservation_status")
    Call<ServerResponse> updateReservationStatus(@Body ServerRequest serverRequest);

}
