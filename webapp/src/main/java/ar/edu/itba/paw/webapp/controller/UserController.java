package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.model.Order;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.model.util.PaginatedResult;
import ar.edu.itba.paw.service.OrderService;
import ar.edu.itba.paw.service.UserService;
import ar.edu.itba.paw.webapp.form.CreateRestaurantForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;

@Controller
public class UserController {
    private static final String DEFAULT_PAGE = "1";
    private static final String DEFAULT_SIZE = "20";

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/orders", method = RequestMethod.GET)
    public ModelAndView myOrders(
            @RequestParam(value = "page", defaultValue = DEFAULT_PAGE) final int page,
            @RequestParam(value = "size", defaultValue = DEFAULT_SIZE) final int size
    ) {
        ModelAndView mav = new ModelAndView("user/myorders");
        if (page < 1 || size < 1) {
            mav.addObject("error", true);
            return mav;
        }
        User currentUser = userService.getByEmail(ControllerHelper.getCurrentUserEmail()).orElse(null);
        PaginatedResult<Order> orders = orderService.getByUser(currentUser.getUserId(), page, size);
        mav.addObject("orders", orders.getResult());
        mav.addObject("orderCount", orders.getTotalCount());
        mav.addObject("pageCount", orders.getTotalPageCount());
        return mav;
    }

    @RequestMapping(value = "/orders/{id:\\d+}", method = RequestMethod.GET)
    public ModelAndView order(@PathVariable int id) {
        ModelAndView mav = new ModelAndView("user/order");
        mav.addObject("order", orderService.getById(id).orElseThrow(IllegalArgumentException::new));
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
    ) {
        // TODO recover images when errors occur on other field
        if (errors.hasErrors()) {
            return createRestaurant(form);
        }

        return new ModelAndView("redirect:/thankyou");
    }
}
