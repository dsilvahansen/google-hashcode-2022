package com.yogi.hashcode;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class ProjectsPlanning {


    /**
     * 3 3
     * Anna 1
     * C++ 2
     * Bob 2
     * HTML 5
     * CSS 5
     * Maria 1
     * Python 3
     * Logging 5 10 5 1
     * C++ 3
     * WebServer 7 10 7 2
     * HTML 3
     * C++ 2
     * WebChat 10 20 20 2
     * Python 3
     * HTML 3
     *
     * @param args
     */
    public static void main(String[] args) throws IOException {
        // projects
        // skills
        // Skill1 -> [{Person1, Skill1, level1}, {Person2, Skill1, level1}]
//        String fileName = "a_an_example.in.txt";
//        String fileName = "b_better_start_small.in.txt";
//        String fileName = "c_collaboration.in.txt";
//        String fileName = "d_dense_schedule.in.txt";
//        String fileName = "e_exceptional_skills.in.txt";
        String fileName = "f_find_great_mentors.in.txt";
        HashMap<String, PriorityQueue<Person>> skillsOfPeopleByLevel = new HashMap<>();
        PriorityQueue<Project> projects = new PriorityQueue<>(Project::compare);
        HashSet<String> listOfSkillNamesFromPeople = new HashSet<>();
        List<Person> listOfPeople = new ArrayList<>();
        List<Project> pickedProjects = new ArrayList<>();
        System.out.println("\nParsing Input start");
        long startInputTime = System.nanoTime();
        parseInput(fileName, skillsOfPeopleByLevel, projects, listOfSkillNamesFromPeople, listOfPeople);
        long endInputTime = System.nanoTime();
        System.out.println("Parsing Input end in " + (endInputTime - startInputTime) / 1000000 + "ms");

        System.out.println("\nRunning for projects start");
        long startAssignTime = System.nanoTime();
        assignContributors(skillsOfPeopleByLevel, projects, pickedProjects);
        long endAssignTime = System.nanoTime();
        System.out.println("Running for projects end in " + (endAssignTime - startAssignTime) / 1000000 + "ms");

//        long duration = (endTime - startTime);  //divide by 1000000 to get milliseconds.

        // assigned for all projects
        System.out.println("\nPrinting output start");
        long startOutputTime = System.nanoTime();
        printOutput(fileName, pickedProjects);
        long endOutputTime = System.nanoTime();
        System.out.println("Printing output end in " + (endOutputTime - startOutputTime) / 1000000 + "ms");

        /**
         * 3
         * WebServer
         * Bob Anna
         * Logging
         * Anna
         * WebChat
         * Maria Bob
         */


    }

    private static void assignContributors(HashMap<String, PriorityQueue<Person>> skillsOfPeopleByLevel, PriorityQueue<Project> projects, List<Project> pickedProjects) {
        int projectInLoop;
        do {
            projectInLoop = 0;
            for (Project project : projects) {
                if(project.projectPlanned){
                    continue;
                }
                // get people
                List<Person> pickedPeople = new ArrayList<>();
                // for every skill in the project, from high level to small level
                for (Skill projectSkill : project.projectSkills) {
                    // c++
//                PriorityQueue<Integer> resourceSkillLevelsNeeded = project.skills.get(resourceSkillNameNeeded);
                    PriorityQueue<Person> peopleEligible = skillsOfPeopleByLevel.getOrDefault(projectSkill.skillName, new PriorityQueue<>());
                    // peopleEligible = c++ -> {10, Nikhil}, {0, Yogi}
                    // resourceSkillNameNeeded  -> resourceSkillLevelsNeeded
                    // c++                      -> [5, 5]
                    Person theGuy = getTheGuy(projectSkill, peopleEligible, pickedPeople);
                    if (theGuy != null) {
                        pickedPeople.add(theGuy);
                        project.skillsInOrder.put(projectSkill, theGuy);
                        theGuy.isAssigned = true;
                    } else {
                        break;
                    }
                }

                if (pickedPeople.size() == project.requiredPeople) {
                    // found required people
                    projectInLoop++;
                    project.projectPlanned = true;
                    incrementSkillAndUsedDays(project);
                    pickedProjects.add(project);
                } else {
                    project.skillsInOrder.keySet().forEach(skill -> project.skillsInOrder.put(skill, null));
                }
                pickedPeople.parallelStream().forEach(p -> {
                    p.isAssigned = false;
                });
            }
        }while (projectInLoop > 0);
    }

    private static void incrementSkillAndUsedDays(Project project) {
        for (Map.Entry<Skill, Person> skillPersonEntry : project.skillsInOrder.entrySet()) {
            Skill projectSkill = skillPersonEntry.getKey();
            Person assignedPerson = skillPersonEntry.getValue();
            // if the project skill is maintained in current skill of the picked person, and if eligible for skill increment, do so
            if ((projectSkill.skillLevel - assignedPerson.getSkillLevel(projectSkill.skillName) >= 0)) {
                assignedPerson.increaseSkillLevel(projectSkill.skillName);
            }
        }
    }

    // iter3 - projects ordered by bestbefore
    // iter4 - projects ordered by score and running as long as atleast one project is picked

    private static void printOutput(String fileName, List<Project> pickedProjects) throws IOException {
        FileWriter fileWriter = new FileWriter("output\\out_iter4_" + fileName);

        fileWriter.write(pickedProjects.size() + "\n");

        for (Project currProject : pickedProjects) {
            fileWriter.write(currProject.projectName + "\n");
            for (Person assignedPerson : currProject.skillsInOrder.values()) {
                fileWriter.write(assignedPerson.personName + " ");
            }
            fileWriter.write("\n");
        }
        fileWriter.close();
    }

    private static Person getTheGuy(Skill projectSkill, PriorityQueue<Person> peopleEligible, List<Person> pickedPeople) {

        for (Person guy : peopleEligible) {
            if (guy.getSkillLevel(projectSkill.skillName) < projectSkill.skillLevel - 1) {
                continue;
            }
            if (!guy.isAssigned || !pickedPeople.contains(guy)) {
                if (guy.getSkillLevel(projectSkill.skillName) >= projectSkill.skillLevel || (projectSkill.skillLevel - guy.getSkillLevel(projectSkill.skillName) == 1 && Person.isSkilledIn(pickedPeople, projectSkill))) {
                    return guy;
                }
            }
        }
        return null;
    }


    // 1. MVP ->
    //  just match skills and choose by project priority of deadline + reward
    //  no mentoring, just choose anyone
    // no time concept

    // 2. add time concept

    // 3. mentoring
    // give preference to growth
    // project priority -> more skills required (idea is to mentor as many people as possible here)
    //  job scheduling, coin problem

    private static void parseInput(String fileName, HashMap<String, PriorityQueue<Person>> skillsOfPeopleByLevel, PriorityQueue<Project> projects, HashSet<String> listOfSkillNames, List<Person> listOfPeople) throws IOException {
        File f = new File("input\\" + fileName);
        Scanner sc = new Scanner(f);

        String[] noOfRequest = sc.nextLine().split(" ");

        for (int i = 0; i < Integer.parseInt(noOfRequest[0]); i++) {
            String[] personAndSkills = sc.nextLine().split(" ");
            Person person = new Person(personAndSkills[0]);
            int noOfSkills = Integer.parseInt(personAndSkills[1]);
            for (int j = 0; j < noOfSkills; j++) {
                String[] skillAndLevel = sc.nextLine().split(" ");
                listOfSkillNames.add(skillAndLevel[0]);
                person.skills.add(new Skill(skillAndLevel[0], Integer.parseInt(skillAndLevel[1]), true));
            }
            listOfPeople.add(person);
        }

        for (Person p : listOfPeople) {
            for (String s : listOfSkillNames) {
                // trying out assigning first available lowest skill guy possible
                PriorityQueue<Person> skillQueue = skillsOfPeopleByLevel.getOrDefault(s,
                        new PriorityQueue<>(Comparator.comparingInt(personA -> personA.getSkillLevel(s))));
                skillQueue.add(p);
                skillsOfPeopleByLevel.put(s, skillQueue);
            }
        }
        //        the name of the project (ASCII string of at most 20 characters, all of which are lowercase or uppercase English alphabet letters a-z and A-Z or numbers 0-9),
        //                an integer Di (1 ≤Di ≤ 105) – the number of days it takes to complete the project,
        //        an integer Si (1 ≤ Si ≤ 105) – the score awarded for project’s completion,
        //        an integer Bi (1 ≤ Bi ≤ 105) – the “best before” day for the project,
        //        an integer Ri (1 ≤ Ri ≤ 100) – the number of roles in the project.
        int noOfProjects = Integer.parseInt(noOfRequest[1]);
        for (int i = 0; i < noOfProjects; i++) {
            String[] projectDesc = sc.nextLine().split(" ");
            String projectName = projectDesc[0];
            int d = Integer.parseInt(projectDesc[1]);
            int s = Integer.parseInt(projectDesc[2]);
            int b = Integer.parseInt(projectDesc[3]);
            int r = Integer.parseInt(projectDesc[4]);
            //  HTML 3
            //     * C++ 2
            //
            PriorityQueue<Skill> projectSkills = new PriorityQueue<>((skillA, skillB) -> skillB.skillLevel - skillA.skillLevel);
            Map<Skill, Person> skillsInOrder = new LinkedHashMap<>();
            for (int j = 0; j < r; j++) {
                String[] skillAndLevel = sc.nextLine().split(" ");
                Skill projectSkill = new Skill(skillAndLevel[0], Integer.parseInt(skillAndLevel[1]), false);
                projectSkills.add(projectSkill);
                skillsInOrder.put(projectSkill, null);
            }
            Project project = new Project(projectName, d, s, b, r, projectSkills, null, skillsInOrder);
            projects.add(project);
        }
    }

    static class Person {
        String personName;
        Set<Skill> skills;
        boolean isAssigned;

        public Person(String personName) {
            this.personName = personName;
            this.skills = new HashSet<>();
            this.isAssigned = false;
        }

        // check if any of the person in the pickedPeople of the project has the said skill at the given level or higher
        public static boolean isSkilledIn(List<Person> pickedPeople, Skill projectSkill) {
            for (Person person : pickedPeople) {
                if (person.getSkillLevel(projectSkill.skillName) >= projectSkill.skillLevel)
                    return true;
            }
            return false;
        }

        @Override
        public String toString() {
            return "Person{" +
                    "personName='" + personName + '\'' +
                    '}';
        }

        public Integer getSkillLevel(String skill) {
            Optional<Skill> personSkill = skills.stream().filter(s -> StringUtils.equals(s.skillName, skill)).findFirst();
            return personSkill.map(s -> s.skillLevel).orElse(0);
        }

        public void increaseSkillLevel(String currentSkill) {
            Optional<Skill> personSkill = skills.stream().filter(s -> StringUtils.equals(s.skillName, currentSkill)).findFirst();
            if (personSkill.isPresent()) {
                personSkill.get().skillLevel++;
            } else {
                skills.add(new Skill(currentSkill, 1, true));
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Person person = (Person) o;
            return personName.equals(person.personName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(personName);
        }
    }

    static class Skill {
        String skillName;
        int skillLevel;
        boolean isPersonSkill;

        public Skill(String skillName, int skillLevel, boolean isPersonSkill) {
            this.skillName = skillName;
            this.skillLevel = skillLevel;
            this.isPersonSkill = isPersonSkill;
        }

        @Override
        public String toString() {
            return "Skill{" +
                    "skillName='" + skillName + '\'' +
                    ", skillLevel=" + skillLevel +
                    '}';
        }

    }

    static class Project {
        String projectName;
        int days;
        int score;
        int bestBefore;
        int requiredPeople;
        PriorityQueue<Skill> projectSkills;
        //        Map<String, PriorityQueue<Integer>> skills;
        Map<Skill, Person> skillsInOrder;
        boolean projectPlanned;

        public Project(String projectName, int days, int score, int bestBefore, int requiredPeople, PriorityQueue<Skill> projectSkills, Map<String, PriorityQueue<Integer>> skills, Map<Skill, Person> skillsInOrder) {
            this.projectName = projectName;
            this.days = days;
            this.score = score;
            this.bestBefore = bestBefore;
            this.requiredPeople = requiredPeople;
            this.projectPlanned = false;
            this.projectSkills = projectSkills;
            this.skillsInOrder = skillsInOrder;
        }

        public static int compare(Project a, Project b) {
            return b.score - a.score;
//            return a.bestBefore - b.bestBefore;
//            return (b.score / (b.days * b.bestBefore)) - (a.score / (a.days * a.bestBefore));
        }

        @Override
        public String toString() {
            return "Project{" +
                    "projectName='" + projectName + '\'' +
                    '}';
        }
        // score/(requiredPeople*days) -> score priority

    }

}
