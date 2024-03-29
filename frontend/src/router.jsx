import {
    createBrowserRouter
} from "react-router-dom";
import Home from "./pages/Home.jsx";
import Error from "./pages/Error.jsx";
import Restaurants from "./pages/Restaurants.jsx";
import UserProfile from "./pages/UserProfile.jsx";
import Login from "./pages/Login.jsx";
import ResetPassword from "./pages/ResetPassword.jsx";
import Register from "./pages/Register.jsx";
import Restaurant from "./pages/Restaurant.jsx";
import VerifyAccount from "./pages/VerifyAccount.jsx";
import ProtectedRoute from "./components/ProtectedRoute.jsx";
import CreateRestaurant from "./pages/CreateRestaurant.jsx";
import UserRestaurants from "./pages/UserRestaurants.jsx";
import ModeratorsPanel from "./pages/ModeratorsPanel.jsx";
import RestaurantOrders from "./pages/RestaurantOrders.jsx";
import UserOrders from "./pages/UserOrders.jsx";

const router = createBrowserRouter(
    [
        {
            path: "/",
            Component: Home
        },
        {
            path: "/auth/login",
            Component: Login
        },
        {
            path: "/auth/register",
            Component: Register
        },
        {
            path: "/auth/reset-password",
            Component: ResetPassword
        },
        {
            path: "/auth/verify",
            Component: VerifyAccount
        },
        {
            path: "/restaurants",
            Component: Restaurants
        },
        {
            path: "/restaurants/create",
            element: <ProtectedRoute><CreateRestaurant/></ProtectedRoute>
        },
        {
            path: "/restaurants/:restaurantId",
            Component: Restaurant
        },
        {
            path: "/restaurants/:restaurantId/edit",
            element: <ProtectedRoute><Restaurant edit={true}/></ProtectedRoute>
        },
        {
            path: "/restaurants/:restaurantId/orders",
            element: <ProtectedRoute><RestaurantOrders/></ProtectedRoute>
        },
        {
            path: "/user",
            element: <ProtectedRoute><UserProfile/></ProtectedRoute>
        },
        {
            path: "/user/orders",
            element: <ProtectedRoute><UserOrders/></ProtectedRoute>
        },
        {
            path: "/user/orders/:orderId",
            element: <ProtectedRoute><UserOrders/></ProtectedRoute>
        },
        {
            path: "/user/restaurants",
            element: <ProtectedRoute><UserRestaurants/></ProtectedRoute>
        },
        {
            path: "/moderators",
            element: <ProtectedRoute moderator={true}><ModeratorsPanel/></ProtectedRoute>
        },
        {
            path: "*",
            element: <Error errorNumber="404"/>
        }
    ],
    {
        basename: import.meta.env.BASE_URL
    }
);

export default router;
