import {
  faHotel,
  faShoppingCart,
  faNotesMedical,
  faCocktail,
  faBoxes,
  faShoppingBag,
  faBus,
  faBolt,
} from "@fortawesome/free-solid-svg-icons";
import { IconDefinition } from "@fortawesome/fontawesome-svg-core";

export type CategoryKey =
  | "ACCOMMODATION"
  | "GROCERIES"
  | "HEALTHCARE"
  | "ENTERTAINMENT"
  | "MISC"
  | "SHOPPING"
  | "TRANSPORTATION"
  | "UTILITIES";

export interface CategoryData {
  label: string;
  icon: IconDefinition;
}

export const CategoryMap: Record<CategoryKey, CategoryData> = {
  ACCOMMODATION: {
    label: "Accommodation",
    icon: faHotel,
  },
  GROCERIES: {
    label: "Groceries",
    icon: faShoppingCart,
  },
  HEALTHCARE: {
    label: "Healthcare",
    icon: faNotesMedical,
  },
  ENTERTAINMENT: {
    label: "Entertainment",
    icon: faCocktail,
  },
  MISC: {
    label: "Miscellaneous",
    icon: faBoxes,
  },
  SHOPPING: {
    label: "Shopping",
    icon: faShoppingBag,
  },
  TRANSPORTATION: {
    label: "Transportation",
    icon: faBus, // or faPlane
  },
  UTILITIES: {
    label: "Utilities",
    icon: faBolt,
  },
};
