/*
 * import {parseLinkHeader} from "@web3-storage/parse-link-header";
 * import Restaurant from "../../data/model/Restaurant.js";
 * import PagedContent from "../../data/model/PagedContent.js";
 *
 *
 * export function userUserService(api) {
 *     const getUser = async (url, query) => {
 *         const response = await api.get(url, {
 *             params: query
 *         });
 *         const links = parseLinkHeader(response.headers?.link, {});
 *         const restaurants = Array.isArray(response.data) ? response.data.map(data => Restaurant.fromJSON(data)) : [];
 *         return new PagedContent(
 *             restaurants,
 *             links?.first,
 *             links?.prev,
 *             links?.next,
 *             links?.last
 *         );
 *     };
 *
 *     return {
 *         getRestaurants
 *     };
 * }
 */
