import { Link } from "react-router-dom";
import "./styles/restaurant_card.styles.css";
import Rating from "./Rating.jsx";
import TagsContainer from "./TagsContainer.jsx";

function RestaurantCard({ restaurantId, mainImage, hoverImage, name, address, rating, ratingCount, tags }) {
    return (
        <>
            <Link className="clickable-object" to={`/restaurants/${restaurantId}`}>
                <div className="card">
                    <div
                        className="card-img"
                        style={{"--main_image": `url(${mainImage})`, "--hover_image": `url(${hoverImage})`}}
                    />
                    <div className="card-body">
                        <div>
                            <h5 className="card-title">{name}</h5>
                            <p className="card-text">{address}</p>
                        </div>
                        <div>
                            <Rating rating={rating} count={ratingCount}/>
                            <TagsContainer tags={tags}/>
                        </div>
                    </div>
                </div>
            </Link>
        </>
    );
}

export default RestaurantCard;
