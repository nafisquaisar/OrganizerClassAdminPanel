package com.example.nafis.nf2024.organizeradminpanel

import javax.security.auth.Subject

object Obj {
    fun classes():Array<String>{
        val classes = arrayOf("Select Class",
            "Class 1", "Class 2", "Class 3", "Class 4", "Class 5",
            "Class 6", "Class 7", "Class 8", "Class 9", "Class 10",
            "Class 11", "Class 12"
        )
        return classes
    }

    fun Subject(clas: String): Array<String> {
        val sub: Array<String> = when (clas) {
            "Class 11","Class 12" ->{
                arrayOf("Select Subject","Math","Physics", "Chemistry","English Grammar")
            }
            "Class 10", "Class 9" -> {
                arrayOf("Select Subject", "Math", "English Grammar", "Physics", "Chemistry", "Bio", "Social Science" ,"Science","Urdu")
            }
            "Class 8", "Class 7", "Class 6", "Class 5" -> {
                arrayOf("Select Subject","Science", "Math", "English Grammar", "Social Science")
            }
            "Class 4", "Class 3" -> {
                arrayOf("Select Subject", "Science", "Math", "English Grammar", "Social Studies")
            }
            "Class 2", "Class 1" -> {
                arrayOf("Select Subject","Science", "Math", "English", "Environmental Studies")
            }
            else -> { // Default case to handle other inputs
                arrayOf("No Subjects Available")
            }
        }
        return sub
    }



    fun Chapter(clas:String,subject:String):Array<String>{

        val chap = when (subject) {
            "Physics" -> {
                when (clas) {
                    "Class 12" -> {
                        arrayOf(
                            "Select Chapter", "All Chapter","Electrostatics", "Current Flow", "Magnetism", "Induction", "AC Circuits",
                            "EM Waves", "Optics", "Dual Nature", "Atoms", "Nuclei", "Semiconductors"
                        )
                    }
                    "Class 11" -> {
                        arrayOf(
                            "Select Chapter", "All Chapter","Physical World", "Vectors", "Measurements", "Straight Line",
                            "Motion Plane", "Motion Laws", "Energy & Power", "Rotation", "Gravitation",
                            "Solid Mechanics", "Fluid Dynamics", "Thermal Props", "Thermodynamics",
                            "Kinetic Theory", "Oscillations", "Waves"
                        )
                    }
                    "Class 10" -> {
                        arrayOf(
                            "Select Chapter", "All Chapter","Electricity", "Magnetism", "Light", "Human Eye", "Energy"
                        )
                    }
                    "Class 9" -> {
                        arrayOf(
                            "Select Chapter","All Chapter", "Motion", "Force & Motion", "Gravitation", "Energy", "Sound"
                        )
                    }
                    "Class 8" -> {
                        arrayOf(
                            "Select Chapter", "All Chapter","Force & Pressure", "Friction", "Sound", "Electric Effects",
                            "Light", "Stars & Solar"
                        )
                    }
                    "Class 7" -> {
                        arrayOf(
                            "Select Chapter", "All Chapter","Heat", "Winds & Cyclones", "Motion & Time", "Electric Effects",
                            "Light"
                        )
                    }
                    "Class 6" -> {
                        arrayOf(
                            "Select Chapter", "All Chapter","Motion & Distance", "Light & Shadows", "Electricity & Circuits",
                            "Fun Magnets"
                        )
                    }
                    else -> arrayOf("No Chapter Available") // Handle case where class is not found
                }
            }
            "Chemistry" -> {
                when (clas) {
                    "Class 12" -> {
                        arrayOf(
                            "Select Chapter",
                            "All Chapter",
                            "Solid State",
                            "Solutions",
                            "Electrochemistry",
                            "Chem Kinetics",
                            "Surface Chem",
                            "Metal Extraction",
                            "p-Block",
                            "d/f-Block",
                            "Coord Compounds",
                            "Haloalkanes",
                            "Alcohols",
                            "Aldehydes",
                            "Nitro Compounds",
                            "Biomolecules",
                            "Polymers",
                            "Daily Chem"
                        )
                    }

                    "Class 11" -> {
                        arrayOf(
                            "Select Chapter",
                            "All Chapter",
                            "Chem Basics",
                            "Atom Structure",
                            "Element Trends",
                            "Chem Bonds",
                            "Matter States",
                            "Thermodynamics",
                            "Equilibrium",
                            "Redox",
                            "Hydrogen",
                            "s-Block",
                            "p-Block",
                            "Org Chem Basics",
                            "Hydrocarbons",
                            "Env Chem"
                        )
                    }

                    "Class 10" -> {
                        arrayOf(
                            "Select Chapter",
                            "All Chapter",
                            "Chem Reactions",
                            "Acids & Bases",
                            "Metals & Non-metals",
                            "Carbon Compounds",
                            "Element Classification"
                        )
                    }

                    "Class 9" -> {
                        arrayOf(
                            "Select Chapter", "All Chapter","Matter Around", "Pure Matter", "Atoms & Molecules",
                            "Atom Structure", "Life Unit"
                        )
                    }

                    "Class 8" -> {
                        arrayOf(
                            "Select Chapter",
                            "All Chapter",
                            "Synthetic Fibres",
                            "Metals & Non-metals",
                            "Coal & Petroleum",
                            "Combustion"
                        )
                    }

                    "Class 7" -> {
                        arrayOf(
                            "Select Chapter","All Chapter", "Acids & Bases", "Chem Changes", "Fibre to Fabric",
                            "Water", "Wastewater"
                        )
                    }

                    "Class 6" -> {
                        arrayOf(
                            "Select Chapter",
                            "All Chapter",
                            "Fibre to Fabric",
                            "Material Sorting",
                            "Substance Separation",
                            "Changes",
                            "Water"
                        )
                    }

                    else -> arrayOf("No Chapter Available") // Handle case where class is not found
                }


            }
            "Bio" -> {
                when (clas) {
                    // For Class 12
                    "Class 12" -> arrayOf(
                        "Select Chapter", "All Chapter","All Chapter", "Reproduction in Organisms", "Human Reproduction", "Reproductive Health",
                        "Principles of Inheritance", "Molecular Basis of Inheritance", "Evolution", "Human Health & Disease",
                        "Strategies for Enhancement in Food Production", "Microbes in Human Welfare", "Biotechnology: Principles and Processes",
                        "Biotechnology and Its Applications", "Organisms and Populations", "Ecosystem", "Biodiversity and Conservation", "Environmental Issues"
                    )

                    // For Class 11
                    "Class 11" -> arrayOf(
                        "Select Chapter", "All Chapter", "Diversity in the Living World", "Structural Organisation in Animals", "Cell Structure and Function",
                        "Biomolecules", "Plant Physiology", "Human Physiology"
                    )

                    // For Class 10
                    "Class 10" -> arrayOf(
                        "Select Chapter", "All Chapter", "Life Cycle", "Coordination", "Reproduction",
                        "Heredity", "Environment", "Resources"
                    )

                    // For Class 9
                    "Class 9" -> arrayOf(
                        "Select Chapter", "All Chapter", "Cells", "Tissues", "Diversity", "Illness",
                        "Resources", "Food"
                    )

                    else -> arrayOf("No Chapter Available")
                }
            }

            "Math" -> {
                when (clas) {
                    "Class 12" -> arrayOf(
                        "Select Chapter", "All Chapter", "Relations", "Inverse Trigonometry", "Matrices",
                        "Determinants", "Continuity", "Derivatives", "Integrals", "Applications of Integrals",
                        "Differentials", "Vectors", "3D Geometry", "Linear Programming", "Probability"
                    )
                    "Class 11" -> arrayOf(
                        "Select Chapter", "All Chapter", "Sets", "Functions", "Trigonometric Functions",
                        "Mathematical Induction", "Complex Numbers", "Inequalities", "Permutations",
                        "Binomial Theorem", "Sequences", "Straight Lines", "Conic Sections",
                        "Introduction to 3D Geometry", "Limits", "Reasoning", "Statistics", "Probability"
                    )
                    "Class 10" -> arrayOf(
                        "Select Chapter", "All Chapter", "Real Numbers", "Polynomials", "Linear Equations",
                        "Quadratic Equations", "Progressions", "Triangles", "Coordinate Geometry", "Trigonometry",
                        "Applications of Trigonometry", "Circles", "Construction", "Areas", "Volumes",
                        "Statistics", "Probability"
                    )
                    "Class 9" -> arrayOf(
                        "Select Chapter", "All Chapter", "Number Systems", "Polynomials", "Coordinate Geometry",
                        "Linear Equations", "Euclid's Geometry", "Lines and Angles", "Triangles",
                        "Quadrilaterals", "Areas", "Circles", "Construction", "Heron's Formula",
                        "Surface Area", "Statistics", "Probability"
                    )
                    "Class 8" -> arrayOf(
                        "Select Chapter", "All Chapter", "Rational Numbers", "Linear Equations", "Quadrilaterals",
                        "Practical Geometry", "Data Handling", "Squares", "Cubes", "Quantities", "Algebra",
                        "Solid Shapes", "Mensuration", "Exponents", "Proportions", "Factorization",
                        "Graphs", "Numbers"
                    )
                    "Class 7" -> arrayOf(
                        "Select Chapter", "All Chapter", "Integers", "Fractions", "Data Handling", "Simple Equations",
                        "Lines and Angles", "Triangles", "Congruence", "Quantities", "Rational Numbers",
                        "Practical Geometry", "Perimeter", "Algebraic Expressions", "Exponents", "Symmetry",
                        "Solid Shapes"
                    )
                    "Class 6" -> arrayOf(
                        "Select Chapter", "All Chapter", "Numbers", "Whole Numbers", "Play with Numbers", "Geometry",
                        "Shapes", "Integers", "Fractions", "Decimals", "Data Handling", "Mensuration", "Ratio",
                        "Symmetry", "Geometry Basics"
                    )
                    "Class 5" -> arrayOf(
                        "Select Chapter", "All Chapter", "Numbers", "Addition and Subtraction", "Multiplication",
                        "Division", "Fractions", "Decimals", "Geometry", "Patterns", "Data Handling", "Shapes",
                        "Measurement", "Perimeter and Area", "Time", "Money", "Graphs"
                    )
                    "Class 4" -> arrayOf(
                        "Select Chapter", "All Chapter", "Numbers", "Addition", "Subtraction", "Multiplication",
                        "Division", "Fractions", "Shapes", "Patterns", "Time", "Money", "Measurement", "Data",
                        "Geometry"
                    )
                    "Class 3" -> arrayOf(
                        "Select Chapter", "All Chapter", "Numbers", "Addition", "Subtraction", "Multiplication",
                        "Division", "Measurement", "Time", "Shapes", "Patterns", "Money", "Data"
                    )
                    "Class 2" -> arrayOf(
                        "Select Chapter", "All Chapter", "Numbers", "Addition and Subtraction", "Patterns",
                        "Shapes", "Measurement", "Money", "Time", "Data"
                    )
                    "Class 1" -> arrayOf(
                        "Select Chapter", "All Chapter", "Numbers", "Addition", "Subtraction", "Patterns",
                        "Shapes", "Measurement", "Time","Money"
                    )
                    else -> arrayOf("No Chapter Available")
                }
            }


            "English Grammar" -> {
                when (clas) {
                    // For Classes 12, 11, 10, 9, 8, 7, 6, and 5
                    "Class 12", "Class 11", "Class 10", "Class 9", "Class 8", "Class 7", "Class 6", "Class 5" -> arrayOf(
                        "Select Chapter", "All Chapter", "Tenses", "Verb Agreement", "Voice",
                        "Speech", "Clauses", "Prepositions", "Determiners", "Transformation"
                    )

                    // For Classes 4, 3, 2, and 1
                    "Class 4", "Class 3", "Class 2", "Class 1" -> arrayOf(
                        "Select Chapter", "All Chapter", "Tenses", "Nouns", "Pronouns", "Verbs",
                        "Adjectives", "Articles", "Prepositions", "Conjunctions"
                    )

                    else -> arrayOf("No Chapter Available")
                }
            }

            "Social Science" -> {
                when (clas) {
                    "Class 8" -> arrayOf(
                        "Select Chapter", "All Chapter", "Resources", "Agriculture", "Minerals & Power",
                        "Industries", "Human Resources"
                    )
                    "Class 7" -> arrayOf(
                        "Select Chapter", "All Chapter", "Environment", "Earth's Core", "Earth's Changes",
                        "Air", "Water", "Wildlife"
                    )
                    "Class 6" -> arrayOf(
                        "Select Chapter", "All Chapter", "Solar System", "Globe", "Earth's Motion",
                        "Maps", "Earth's Domains"
                    )
                    "Class 5" -> arrayOf(
                        "Select Chapter", "All Chapter", "Our Earth", "Continents", "Oceans", "India", "Environment"
                    )
                    "Class 4" -> arrayOf(
                        "Select Chapter", "All Chapter", "Our Surroundings", "Landforms", "Weather", "Seasons",
                        "Community Helpers"
                    )
                    "Class 3" -> arrayOf(
                        "Select Chapter", "All Chapter", "My Family", "My Neighborhood", "Transport",
                        "Communication", "Maps"
                    )
                    "Class 2" -> arrayOf(
                        "Select Chapter", "All Chapter", "Our Home", "Places Around Us", "Work and Play",
                        "Weather", "Safety Rules"
                    )
                    "Class 1" -> arrayOf(
                        "Select Chapter", "All Chapter", "Myself", "My School", "My Friends", "My Neighborhood",
                        "Good Manners"
                    )

                    else -> arrayOf("No Chapter Available")
                }
            }

            "Science" -> {
                when (clas) {

                    "Class 10" -> arrayOf(
                        "Select Chapter", "All Chapter", "Chemical Reactions", "Acids and Bases", "Metals and Non-metals",
                        "Carbon Compounds", "Periodic Table", "Life Processes", "Control & Coordination",
                        "Reproduction", "Heredity & Evolution", "Light", "Human Eye",
                        "Electricity", "Magnetic Effects", "Energy Sources"
                    )

                    "Class 9" -> arrayOf(
                        "Select Chapter", "All Chapter", "Matter", "Atoms", "Atomic Structure",
                        "Cell", "Tissues", "Diversity", "Motion", "Force",
                        "Gravitation", "Work & Energy", "Sound", "Health", "Resources"
                    )


                    "Class 8" -> arrayOf(
                        "Select Chapter", "All Chapter", "Crop Production", "Microorganisms", "Fibers & Plastics",
                        "Metals & Non-metals", "Cell Structure", "Animal Reproduction", "Force & Pressure", "Sound"
                    )
                    "Class 7" -> arrayOf(
                        "Select Chapter", "All Chapter", "Plant Nutrition", "Animal Nutrition", "Fiber to Fabric",
                        "Heat", "Acids & Bases", "Respiration", "Transportation", "Motion & Time"
                    )
                    "Class 6" -> arrayOf(
                        "Select Chapter", "All Chapter", "Food Source", "Food Components", "Separation",
                        "Changes", "Plant Survival", "Distance", "Light & Shadows", "Electricity"
                    )
                    "Class 5" -> arrayOf(
                        "Select Chapter", "All Chapter", "Living Things", "Plant Parts", "Animal Homes",
                        "Materials & Their Uses", "States of Matter", "Water & Air", "Soil", "Force & Energy"
                    )
                    "Class 4" -> arrayOf(
                        "Select Chapter", "All Chapter", "Plants Around Us", "Animals Around Us", "Water Cycle",
                        "Air & Breathing", "Earth & Sky", "Weather", "Magnets", "Simple Machines"
                    )
                    "Class 3" -> arrayOf(
                        "Select Chapter", "All Chapter", "Living & Non-Living", "Plants & Trees", "Animals & Insects",
                        "Water & Weather", "Sky & Stars", "Rocks & Minerals", "Electricity Basics", "Forces Around Us"
                    )
                    "Class 2" -> arrayOf(
                        "Select Chapter", "All Chapter", "Our Body", "Plants & Animals", "Weather & Seasons",
                        "Air & Water", "Types of Materials", "Light & Shadows", "Magnets", "Simple Experiments"
                    )
                    "Class 1" -> arrayOf(
                        "Select Chapter", "All Chapter", "My Body", "Plants & Trees", "Animals & Birds",
                        "Water", "Sky & Earth", "Weather", "Basic Forces", "Simple Machines"
                    )

                    else -> arrayOf("No Chapter Available")
                }
            }

            "Urdu"->{
                when(clas){
                    "Class 9" -> arrayOf(
                        "Select Chapter", "All Chapter", "قاضی کی کہانی", "شہیدوں کی دنیا", "مٹی کی محبت",
                        "خدا کی کرشمہ", "نظامِ طبیعت", "علم کی برکت", "حمد", "نعت",
                        "غزل", "نظم"
                    )
                    "Class 10" -> arrayOf(
                        "Select Chapter", "All Chapter", "تہذیب اور تعمیر", "سوانح سر سید", "مضامین اور رسائل",
                        "سائنس اور اسلام", "زبان اور تعلیم", "تصورِ حیات", "مرثیہ", "نظم",
                        "غزل", "رباعیات"
                    )
                    else -> arrayOf("Select Chapter")
                }
            }

            else -> arrayOf("Select Chapter") // Handle case where subject is not "Physics"
        }
     return chap
    }

    fun Type() :Array<String>{
        val types= arrayOf("Select Type","Note","Video")
        return types
    }

    fun Time(): Array<String> {
        val times = arrayOf("Select Time", "10 minutes", "15 minutes", "20 minutes", "30 minutes", "60 minutes", "120 minutes", "180 minutes")
        return times
    }

    fun Board() :Array<String>{
        val board= arrayOf("Select Board","Bihar Board","MP Board","CBSE")
        return board
    }
}
