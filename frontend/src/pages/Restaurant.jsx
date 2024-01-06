import React, {useState} from "react";
import {useParams} from "react-router-dom";
import Page from "../components/Page.jsx";
import {useQueries, useQuery} from "@tanstack/react-query";
import {useTranslation} from "react-i18next";
import {useApi} from "../hooks/useApi.js";
import {useContext} from "react";
import ApiContext from "../contexts/ApiContext.jsx";
import {useRestaurantService} from "../hooks/services/useRestaurantService.js";
import Error from "./Error.jsx";
import ContentLoader from "react-content-loader";
import "./styles/restaurant.styles.css";
import Rating from "../components/Rating.jsx";
import TagsContainer from "../components/TagsContainer.jsx";
import ProductCard from "../components/ProductCard.jsx";

function Restaurant() {
    const DECIMAL_DIGITS = 2;

    const { t } = useTranslation();
    const api = useApi();
    const apiContext = useContext(ApiContext);
    const restaurantService = useRestaurantService(api);

    const { restaurantId } = useParams();
    const { isError: restaurantIsError, data: restaurant, error: restaurantError } = useQuery({
        queryKey: ["restaurant", restaurantId],
        queryFn: async () => (
            await restaurantService.getRestaurant(`${apiContext.restaurantsUrl}/${restaurantId}`, true)
        )
    });
    const { isPending, isError: categoriesIsError, data: categories, error: categoriesError} = useQuery({
        queryKey: ["restaurant", restaurantId, "categories"],
        queryFn: async () => (
            await restaurantService.getCategories(restaurant.categoriesUrl)
        ),
        enabled: !!restaurant
    });
    const products = useQueries({
        queries: categories
            ?
            categories.sort((a, b) => a.orderNum < b.orderNum).map(category => {
                return {
                    queryKey: ["restaurant", restaurantId, "category", category.orderNum, "products"],
                    queryFn: async () => (
                        await restaurantService.getProducts(category.productsUrl)
                    )
                };
            })
            :
            []
    });

    const [cart, setCart] = useState([]);
    const addProductToCart = (productId, name, price, quantity, comments) => {
        setCart([...cart, {productId: productId, name: name, price: price, quantity: quantity, comments: comments}]);
    };

    if (restaurantIsError) {
        return (
            <>
                <Error errorNumber={restaurantError.response.status}/>
            </>
        );
    } else if (categoriesIsError) {
        return (
            <>
                <Error errorNumber={categoriesError.response.status}/>
            </>
        );
    } else if (products.some(product => product.isError)) {
        return (
            <>
                <Error errorNumber="500"/>
            </>
        );
    } else if (isPending || products.some(product => product.isPending)) {
        return (
            <>
                <Page title={t("titles.loading")} className="restaurant">
                    <ContentLoader backgroundColor="#eaeaea" foregroundColor="#e0e0e0" width="1920">
                        <rect x="288" y="50" rx="0" ry="0" width="1344" height="200"/>
                        <rect x="12" y="300" rx="0" ry="0" width="200" height="380"/>
                        <rect x="236" y="300" rx="0" ry="0" width="1268" height="64"/>
                        <rect x="236" y="388" rx="0" ry="0" width="400" height="200"/>
                        <rect x="656" y="388" rx="0" ry="0" width="400" height="200"/>
                        <rect x="1528" y="300" rx="0" ry="0" width="380" height="120"/>
                    </ContentLoader>
                </Page>
            </>
        );
    }
    return (
        <>
            <Page title={restaurant.name} className="restaurant">
                <div className="header">
                    <img src={restaurant.portrait1Url} alt={restaurant.name}/>
                </div>
                <div className="d-flex justify-content-center">
                    <div className="information">
                        <img src={restaurant.logoUrl} alt={restaurant.name} className="logo"/>
                        <div className="flex-grow-1">
                            <h1>{restaurant.name}</h1>
                            <p className="mb-1">
                                {restaurant.description || <i>{t("restaurant.no_description")}</i>}
                            </p>
                            <p><i className="bi bi-geo-alt"></i> {restaurant.address}</p>
                            {
                                restaurant.reviewCount === 0
                                    ?
                                    <small className="text-muted">{t("restaurant.no_reviews")}</small>
                                    :
                                    <>
                                        <Rating rating={restaurant.averageRating} count={restaurant.reviewCount}/>
                                        <button className="btn btn-link" type="button">
                                            <small>{t("restaurant.view_reviews")}</small>
                                        </button>
                                    </>
                            }
                            <TagsContainer tags={restaurant.tags} clickable={true}/>
                        </div>
                        <div className="d-flex flex-column gap-2">
                            <button className="btn btn-secondary" type="button">{t("restaurant.edit_menu")}</button>
                            <button className="btn btn-secondary" type="button">{t("restaurant.see_orders")}</button>
                            <button className="btn btn-danger" type="button">{t("restaurant.delete_restaurant")}</button>
                        </div>
                    </div>
                </div>
                <div className="content">
                    <div className="categories sticky">
                        <div className="card">
                            <div className="card-header text-muted">{t("restaurant.categories")}</div>
                            <div className="card-body">
                                <div className="nav nav-pills small">
                                    {
                                        categories.sort((a, b) => a.orderNum < b.orderNum).map(category => (
                                            <button
                                                className="category-item nav-link"
                                                key={category.orderNum}
                                                onClick={(event) => {
                                                    document.querySelector(`#category-${category.orderNum}`).scrollIntoView({block: "start"});
                                                    document.querySelector(".category-item.active")?.classList.remove("active");
                                                    event.currentTarget.classList.add("active");
                                                }}
                                            >
                                                {category.name}
                                            </button>
                                        ))
                                    }
                                </div>
                            </div>
                        </div>
                    </div>
                    <div className="menu px-4">
                        {
                            categories.sort((a, b) => a.orderNum < b.orderNum).map((category, i) => (
                                <React.Fragment key={category.orderNum}>
                                    <div className="card mb-4" id={`category-${category.orderNum}`}>
                                        <div className="card-body d-flex justify-content-between align-items-center">
                                            <h3 className="mb-0">{category.name}</h3>
                                        </div>
                                    </div>
                                    <div className="product-container">
                                        {
                                            products[i].data.map(product => (
                                                <ProductCard
                                                    key={product.productId}
                                                    productId={product.productId}
                                                    name={product.name}
                                                    description={product.description}
                                                    price={product.price}
                                                    imageUrl={product.imageUrl}
                                                    addProductToCart={addProductToCart}
                                                />
                                            ))
                                        }
                                    </div>
                                </React.Fragment>
                            ))
                        }
                    </div>
                    <div className="cart sticky">
                        <div className="card">
                            <div className="card-header text-muted">{t("restaurant.my_order")}</div>
                            <ul className="list-group list-group-flush">
                                {
                                    cart.map((product) => (
                                        <li className="list-group-item d-flex justify-content-between" key={product.productId}>
                                            <div className="d-flex align-items-center gap-1">
                                                <span className="badge text-bg-secondary">x{product.quantity}</span>
                                                <span>{product.name}</span>
                                            </div>
                                            <span className="no-wrap"><strong>${(product.price * product.quantity).toFixed(DECIMAL_DIGITS)}</strong></span>
                                        </li>
                                    ))
                                }
                            </ul>
                            <div className="card-body d-flex">
                                <button className="btn btn-primary flex-grow-1" id="place-order-button" type="button">
                                    {t("restaurant.place_order")}
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            </Page>
        </>
    );
}

export default Restaurant;
