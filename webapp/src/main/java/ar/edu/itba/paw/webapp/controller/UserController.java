package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.model.*;
import ar.edu.itba.paw.model.util.PaginatedResult;
import ar.edu.itba.paw.model.util.Pair;
import ar.edu.itba.paw.service.*;
import ar.edu.itba.paw.webapp.auth.PawAuthUserDetails;
import ar.edu.itba.paw.webapp.exception.RestaurantNotFoundException;
import ar.edu.itba.paw.webapp.form.*;
import ar.edu.itba.paw.webapp.exception.OrderNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@Controller
public class UserController {
    private static final int DEFAULT_ORDERS_PAGE_SIZE = 20;

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductService productService;

    private ModelAndView myOrders(
            @Valid final PagingForm paging,
            final BindingResult errors,
            final String status
    ) {
        ModelAndView mav = new ModelAndView("user/myorders");

        if (errors.hasErrors()) {
            mav.addObject("error", Boolean.TRUE);
            paging.clear();
        }

        PaginatedResult<OrderItemless> orders;
        if (status.equals("in progress")) {
            orders = orderService.getInProgressByUserExcludeItems(ControllerUtils.getCurrentUserIdOrThrow(), paging.getPageOrDefault(), paging.getSizeOrDefault(DEFAULT_ORDERS_PAGE_SIZE));
        } else {
            orders = orderService.getByUserExcludeItems(ControllerUtils.getCurrentUserIdOrThrow(), paging.getPageOrDefault(), paging.getSizeOrDefault(DEFAULT_ORDERS_PAGE_SIZE));
        }

        mav.addObject("orders", orders.getResult());
        mav.addObject("orderCount", orders.getTotalCount());
        mav.addObject("pageCount", orders.getTotalPageCount());
        mav.addObject("status", status);

        return mav;
    }

    @RequestMapping(value = "/user/orders", method = RequestMethod.GET)
    public ModelAndView myOrders() {
        return new ModelAndView("redirect:/user/orders/pending");
    }

    @RequestMapping(value = "/user/orders/pending", method = RequestMethod.GET)
    public ModelAndView myOrdersPending(
            @Valid final PagingForm paging,
            final BindingResult errors
    ) {
        return myOrders(paging, errors, "in progress");
    }

    @RequestMapping(value = "/user/orders/all", method = RequestMethod.GET)
    public ModelAndView myOrdersAll(
            @Valid final PagingForm paging,
            final BindingResult errors
    ) {
        return myOrders(paging, errors, "all");
    }

    @RequestMapping(value = "/orders/{id:\\d+}", method = RequestMethod.GET)
    public ModelAndView order(@PathVariable int id) {
        ModelAndView mav = new ModelAndView("user/order");
        mav.addObject("order", orderService.getById(id).orElseThrow(OrderNotFoundException::new));
        return mav;
    }

    @RequestMapping(value = "/restaurants/create", method = RequestMethod.GET)
    public ModelAndView createRestaurant(
            @ModelAttribute("createRestaurantForm") final CreateRestaurantForm form
    ) {
        return new ModelAndView("user/create_restaurant");
    }

    @RequestMapping(value = "/restaurants/create", method = RequestMethod.POST)
    public ModelAndView createRestaurantForm(
            @Valid @ModelAttribute("createRestaurantForm") final CreateRestaurantForm form,
            final BindingResult errors
    ) throws IOException {
        // TODO recover images when errors occur on other field
        if (errors.hasErrors()) {
            return createRestaurant(form);
        }

        PawAuthUserDetails userDetails = ControllerUtils.getCurrentUserDetailsOrThrow();
        int restaurantId = restaurantService.create(
                form.getName(),
                userDetails.getUsername(),
                userDetails.getUserId(),
                form.getDescription(),
                form.getAddress(),
                form.getMaxTables(),
                form.getLogo().getBytes(),
                form.getPortrait1().getBytes(),
                form.getPortrait2().getBytes()
        );

        return new ModelAndView(String.format("redirect:/restaurants/%d", restaurantId));
    }

    @RequestMapping(value = "/restaurants/{id:\\d+}/edit", method = RequestMethod.GET)
    public ModelAndView editRestaurant(
            @PathVariable final int id,
            @ModelAttribute("addProductForm") final AddProductForm addProductForm,
            @ModelAttribute("addCategoryForm") final AddCategoryForm addCategoryForm,
            @ModelAttribute("deleteProductForm") final DeleteProductForm deleteProductForm,
            @ModelAttribute("deleteCategoryForm") final DeleteCategoryForm deleteCategoryForm,
            final Boolean addProductErrors,
            final Boolean addCategoryErrors
    ) {
        ModelAndView mav = new ModelAndView("user/edit_menu");

        final Restaurant restaurant = restaurantService.getById(id).orElseThrow(RestaurantNotFoundException::new);
        mav.addObject("restaurant", restaurant);
        final List<Pair<Category, List<Product>>> menu = restaurantService.getMenu(id);
        mav.addObject("menu", menu);

        mav.addObject("addProductErrors", addProductErrors);
        mav.addObject("addCategoryErrors", addCategoryErrors);

        return mav;
    }

    @RequestMapping(value = "/restaurants/{id:\\d+}/edit/add_product", method = RequestMethod.POST)
    public ModelAndView addProductToRestaurant(
            @PathVariable final int id,
            @Valid @ModelAttribute("addProductForm") final AddProductForm addProductForm,
            final BindingResult errors,
            @ModelAttribute("addCategoryForm") final AddCategoryForm addCategoryForm,
            @ModelAttribute("deleteProductForm") final DeleteProductForm deleteProductForm,
            @ModelAttribute("deleteCategoryForm") final DeleteCategoryForm deleteCategoryForm
    ) throws IOException {
        if (errors.hasErrors()) {
            return editRestaurant(id, addProductForm, addCategoryForm, deleteProductForm, deleteCategoryForm, true, false);
        }

        productService.create(
                addProductForm.getCategoryId(),
                addProductForm.getProductName(),
                addProductForm.getDescription(),
                addProductForm.getImage().getBytes(),
                addProductForm.getPrice()
        );

        return new ModelAndView(String.format("redirect:/restaurants/%d/edit", id));
    }

    @RequestMapping(value = "/restaurants/{id:\\d+}/edit/add_category", method = RequestMethod.POST)
    public ModelAndView addCategoryToRestaurant(
            @PathVariable final int id,
            @ModelAttribute("addProductForm") final AddProductForm addProductForm,
            @Valid @ModelAttribute("addCategoryForm") final AddCategoryForm addCategoryForm,
            final BindingResult errors,
            @ModelAttribute("deleteProductForm") final DeleteProductForm deleteProductForm,
            @ModelAttribute("deleteCategoryForm") final DeleteCategoryForm deleteCategoryForm
    ) {
        if (errors.hasErrors()) {
            return editRestaurant(id, addProductForm, addCategoryForm, deleteProductForm, deleteCategoryForm, false, true);
        }

        categoryService.create(addCategoryForm.getRestaurantId(), addCategoryForm.getName());

        return new ModelAndView(String.format("redirect:/restaurants/%d/edit", id));
    }

    @RequestMapping(value = "/restaurants/{id:\\d+}/edit/delete_product", method = RequestMethod.POST)
    public ModelAndView deleteProductForRestaurant(
            @PathVariable final int id,
            @ModelAttribute("addProductForm") final AddProductForm addProductForm,
            @ModelAttribute("addCategoryForm") final AddCategoryForm addCategoryForm,
            @Valid @ModelAttribute("deleteProductForm") final DeleteProductForm deleteProductForm,
            final BindingResult errors,
            @ModelAttribute("deleteCategoryForm") final DeleteCategoryForm deleteCategoryForm
    ) {
        if (errors.hasErrors()) {
            throw new IllegalStateException();
        }

        productService.delete(deleteProductForm.getProductId());

        return new ModelAndView(String.format("redirect:/restaurants/%d/edit", id));
    }

    @RequestMapping(value = "/restaurants/{id:\\d+}/edit/delete_category", method = RequestMethod.POST)
    public ModelAndView deleteCategoryForRestaurant(
            @PathVariable final int id,
            @ModelAttribute("addProductForm") final AddProductForm addProductForm,
            @ModelAttribute("addCategoryForm") final AddCategoryForm addCategoryForm,
            @ModelAttribute("deleteProductForm") final DeleteProductForm deleteProductForm,
            @Valid @ModelAttribute("deleteCategoryForm") final DeleteCategoryForm deleteCategoryForm,
            final BindingResult errors
    ) {
        if (errors.hasErrors()) {
            throw new IllegalStateException();
        }

        categoryService.delete(deleteCategoryForm.getCategoryId());

        return new ModelAndView(String.format("redirect:/restaurants/%d/edit", id));
    }
}
