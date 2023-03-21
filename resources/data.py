from enum import Enum, auto
from recipes import fluid_ingredient
from constants import *

from mcresources import ResourceManager, loot_tables
from mcresources.type_definitions import Json

from constants import *
from mcresources import utils

from recipes import fluid_ingredient


class Size(Enum):
    tiny = auto()
    very_small = auto()
    small = auto()
    normal = auto()
    large = auto()
    very_large = auto()
    huge = auto()


class Weight(Enum):
    very_light = auto()
    light = auto()
    medium = auto()
    heavy = auto()
    very_heavy = auto()


class Category(Enum):
    fruit = auto()
    vegetable = auto()
    grain = auto()
    bread = auto()
    dairy = auto()
    meat = auto()
    cooked_meat = auto()
    other = auto()


def generate(rm: ResourceManager):
    food_item(rm, 'minced_beef', 'farmersdelight:minced_beef', Category.meat, 2, 0, 0, 3, protein=0.5)
    food_item(rm, 'beef_patty', 'farmersdelight:beef_patty', Category.cooked_meat, 2, 1, 0, 1, protein=1.0)
    food_item(rm, 'chicken_cuts', 'farmersdelight:chicken_cuts', Category.meat, 2, 0, 0, 3, protein=0.75)
    food_item(rm, 'cooked_chicken_cuts', 'farmersdelight:cooked_chicken_cuts', Category.cooked_meat, 2, 1, 0, 1,
              protein=1)
    food_item(rm, 'bacon', 'farmersdelight:bacon', Category.meat, 2, 0, 0, 3, protein=0.5)
    food_item(rm, 'cooked_bacon', 'farmersdelight:cooked_bacon', Category.cooked_meat, 2, 1, 0, 2, protein=1)
    food_item(rm, 'mutton_chop', 'farmersdelight:mutton_chops', Category.meat, 2, 0, 0, 3, protein=0.5)
    food_item(rm, 'cooked_mutton_chop', 'farmersdelight:cooked_mutton_chops', Category.cooked_meat, 2, 1, 0, 2,
              protein=1)
    food_item(rm, 'ham', 'farmersdelight:ham', Category.meat, 5, 0, 0, 3, protein=2.5)
    food_item(rm, 'smoked_ham', 'farmersdelight:smoked_ham', Category.cooked_meat, 5, 3, 0, 1.5, protein=5)

    food_item(rm, 'barbecue_stick', 'farmersdelight:barbecue_stick', Category.cooked_meat, 5, 3, 0, 0.75, protein=1,
              veg=2.5)

    food_item(rm, 'cod_slice', 'farmersdelight:cod_slice', Category.meat, 2, 0, 0, 3, protein=0.5)
    food_item(rm, 'cooked_cod_slice', 'farmersdelight:salmon_slice', Category.meat, 2, 0, 0, 3, protein=0.5)
    food_item(rm, 'salmon_slice', 'farmersdelight:cooked_cod_slice', Category.cooked_meat, 2, 1, 0, 2, protein=1)
    food_item(rm, 'cooked_salmon_slice', 'farmersdelight:cooked_salmon_slice', Category.cooked_meat, 2, 1, 0, 2,
              protein=1)

    food_item(rm, 'salmon_roll', 'farmersdelight:salmon_roll', Category.cooked_meat, 2, 1, 0, 2.25, protein=1,
              grain=0.5)
    food_item(rm, 'cod_roll', 'farmersdelight:cod_roll', Category.cooked_meat, 2, 1, 0, 2.25, protein=1, grain=0.5)
    food_item(rm, 'kelp_roll', 'farmersdelight:kelp_roll', Category.cooked_meat, 5, 3, 0, 2, protein=1, grain=0.5,
              veg=0.5)
    food_item(rm, 'kelp_roll_slice', 'farmersdelight:kelp_roll_slice', Category.cooked_meat, 2, 1, 0, 2.25, protein=1,
              grain=0.5, veg=0.5)
    food_item(rm, 'cabbage_rolls', 'farmersdelight:cabbage_rolls', Category.cooked_meat, 4, 2, 0, 1.5, protein=1, veg=1)

    food_item(rm, 'cooked_rice', 'farmersdelight:cooked_rice', Category.grain, 4, 2, 5, 1.5, grain=1)

    food_item(rm, 'egg_sandwich', 'farmersdelight:egg_sandwich', Category.grain, 5, 3, 0, 1.25, protein=3, dairy=0.6,
              grain=1.5)
    food_item(rm, 'chicken_sandwich', 'farmersdelight:chicken_sandwich', Category.grain, 5, 4, 0, 1.25, protein=1.5,
              veg=2, grain=1.5)
    food_item(rm, 'hamburger', 'farmersdelight:hamburger', Category.grain, 6, 4, 0, 1.25, protein=2, veg=3, grain=1.5)
    food_item(rm, 'mutton_wrap', 'farmersdelight:mutton_wrap', Category.grain, 5, 3, 0, 1.25, protein=2, veg=2,
              grain=1.5)
    food_item(rm, 'mixed_salad', 'farmersdelight:mixed_salad', Category.vegetable, 5, 1, 0, 1.25, veg=3.5)


def decayable(rm: ResourceManager, name_parts: utils.ResourceIdentifier, ingredient: utils.Json, category: Category,
              decay: float = 3):
    food_item(rm, name_parts, ingredient, category, 4, 0, 0, decay)


def food_item(rm: ResourceManager, name_parts: utils.ResourceIdentifier, ingredient: utils.Json, category: Category,
              hunger: int, saturation: float, water: int, decay: float, fruit: Optional[float] = None,
              veg: Optional[float] = None, protein: Optional[float] = None, grain: Optional[float] = None,
              dairy: Optional[float] = None):
    rm.item_tag('tfc:foods', ingredient)
    rm.data(('tfc', 'food_items', name_parts), {
        'ingredient': utils.ingredient(ingredient),
        'category': category.name,
        'hunger': hunger,
        'saturation': saturation,
        'water': water if water != 0 else None,
        'decay_modifier': decay,
        'fruit': fruit,
        'vegetables': veg,
        'protein': protein,
        'grain': grain,
        'dairy': dairy
    })


def drinkable(rm: ResourceManager, name_parts: utils.ResourceIdentifier, fluid: utils.Json,
              thirst: Optional[int] = None, intoxication: Optional[int] = None, effects: Optional[utils.Json] = None,
              food: Optional[utils.Json] = None):
    rm.data(('tfc', 'drinkables', name_parts), {
        'ingredient': fluid_ingredient(fluid),
        'thirst': thirst,
        'intoxication': intoxication,
        'effects': effects,
        'food': food
    })


def item_size(rm: ResourceManager, name_parts: utils.ResourceIdentifier, ingredient: utils.Json, size: Size,
              weight: Weight):
    rm.data(('tfc', 'item_sizes', name_parts), {
        'ingredient': utils.ingredient(ingredient),
        'size': size.name,
        'weight': weight.name
    })


def climate_range(rm: ResourceManager, name_parts: utils.ResourceIdentifier, hydration: Tuple[int, int, int] = None,
                  temperature: Tuple[float, float, float] = None):
    data = {}
    if hydration is not None:
        data.update(
            {'min_hydration': hydration[0], 'max_hydration': hydration[1], 'hydration_wiggle_range': hydration[2]})
    if temperature is not None:
        data.update({'min_temperature': temperature[0], 'max_temperature': temperature[1],
                     'temperature_wiggle_range': temperature[2]})
    rm.data(('tfc', 'climate_ranges', name_parts), data)


def hydration_from_rainfall(rainfall: int) -> int:
    return rainfall * 60 // 500


def block_and_item_tag(rm: ResourceManager, name_parts: utils.ResourceIdentifier, *values: utils.ResourceIdentifier,
                       replace: bool = False):
    rm.block_tag(name_parts, *values, replace=replace)
    rm.item_tag(name_parts, *values, replace=replace)
