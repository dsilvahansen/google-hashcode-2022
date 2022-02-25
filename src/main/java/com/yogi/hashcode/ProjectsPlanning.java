package com.yogi.hashcode;

import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.io.FileNotFoundException;
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

        String fileName = "a_an_example.in.txt";
//        String fileName = "b_better_start_small.in.txt";
//        String fileName = "c_collaboration.in.txt";
//        String fileName = "d_dense_schedule.in.txt";
//        String fileName = "e_exceptional_skills.in.txt";
//        String fileName = "f_find_great_mentors.in.txt";
        HashMap<String, PriorityQueue<Person>> people = new HashMap<>();
        PriorityQueue<Project> projects = new PriorityQueue<>(Project::compare);
        HashSet<String> listOfSkillNamesFromPeople = new HashSet<>();
        List<Person> listOfPeople = new ArrayList<>();
        List<Pair<Project, Map<String,List<Person>>>> pickedProjects = new ArrayList<>();
        parseInput(fileName, people, projects, listOfSkillNamesFromPeople, listOfPeople);

        for (Project project : projects) {
            // get people

            List<Person> pickedPeople = new ArrayList<>();
            Map<String, List<Person>> pickedPeopleMapBySkill = new HashMap<>();
            for (String resourceSkillNameNeeded : project.skills.keySet()) {
                // c++
                PriorityQueue<Integer> resourceSkillLevelsNeeded = project.skills.get(resourceSkillNameNeeded);
                PriorityQueue<Person> peopleEligible = people.getOrDefault(resourceSkillNameNeeded, new PriorityQueue<>());
                // peopleEligible = c++ -> {10, Nikhil}, {0, Yogi}
                for (Integer skillLevel : resourceSkillLevelsNeeded) {
                    // resourceSkillNameNeeded  -> resourceSkillLevelsNeeded
                    // c++                      -> [5, 5]
                    Person theGuy = getTheGuy(resourceSkillNameNeeded, skillLevel, peopleEligible);
                    if (theGuy != null) {
                        pickedPeople.add(theGuy);
                        List<Person> list = pickedPeopleMapBySkill.getOrDefault(resourceSkillNameNeeded, new ArrayList<>());
                        list.add(theGuy);
                        pickedPeopleMapBySkill.put(resourceSkillNameNeeded,list);
                    } else {
                        break;
                    }
                    theGuy.isAssigned = true;
                }

            }

            if (pickedPeople.size() == project.requiredPeople) {
                // found required people
                project.projectPlanned = true;
                pickedProjects.add(Pair.of(project, pickedPeopleMapBySkill));
            } else {
                pickedPeople.stream().forEach(p -> p.isAssigned = false);
            }

        }

        // assigned for all projects
        printOutput(fileName, pickedProjects);

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

    private static void printOutput(String fileName, List<Pair<Project, Map<String, List<Person>>>> pickedProjects) throws IOException {
        FileWriter fileWriter = new FileWriter("output\\out_" + fileName);

        fileWriter.write(pickedProjects.size() + "\n");

        for (Pair<Project, Map<String, List<Person>>> project : pickedProjects) {
            Project currProject = project.getLeft();
            fileWriter.write(currProject.projectName + "\n");

            for (Pair<String, Integer> p :project.getLeft().skillsInOrder) {
                //findIndex
                String skillName = p.getLeft();
                List<Person> peeps = project.getRight().get(skillName);
                for(Person peep: peeps){
                    if(peep.skills.get(skillName)>=p.getRight()){
                        fileWriter.write(peep.personName + " ");
                        peeps.remove(peep);
                        break;
                    }
                }
            }
            fileWriter.write("\n");
        }
        fileWriter.close();
    }

    private static Person getTheGuy(String resourceSkillNameNeeded, Integer skillLevel, PriorityQueue<Person> peopleEligible) {
        for (Person guy : peopleEligible) {
            if (guy.getSkillLevel(resourceSkillNameNeeded) < skillLevel) {
                return null;
            }
            if (!guy.isAssigned) {
                return guy;
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


    static class Person {
        String personName;
        Map<String, Integer> skills;
        boolean isAssigned;
        List<Project> projectsAssigned;

        public Person(String personName) {
            this.personName = personName;
            this.skills = new HashMap<>();
            this.isAssigned = false;
            this.projectsAssigned = new ArrayList<>();
        }

        public int getSkillLevel(String skill) {
            return skills.getOrDefault(skill, 0);
        }
    }

    static class Project {
        String projectName;
        int days;
        int score;
        int bestBefore;
        int requiredPeople;
        Map<String, PriorityQueue<Integer>> skills;
        List<Pair<String, Integer>> skillsInOrder;
        boolean projectPlanned;

        public Project(String projectName, int days, int score, int bestBefore, int requiredPeople, Map<String, PriorityQueue<Integer>> skills, List<Pair<String, Integer>> skillsInOrder) {
            this.projectName = projectName;
            this.days = days;
            this.score = score;
            this.bestBefore = bestBefore;
            this.requiredPeople = requiredPeople;
            this.projectPlanned = false;
            this.skills = skills;
            this.skillsInOrder = skillsInOrder;
        }

        public static int compare(Project a, Project b) {
            return b.score - a.score;
        }
        // score/(requiredPeople*days) -> score priority

    }


//
//    c++ -> {4,a},{4,b},{3,c},{1,d}, {0, e}
//    python -> {1,a}

    private static void parseInput(String fileName, HashMap<String, PriorityQueue<Person>> people, PriorityQueue<Project> projects, HashSet<String> listOfSkillNames, List<Person> listOfPeople) throws IOException {
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
                person.skills.put(skillAndLevel[0], Integer.parseInt(skillAndLevel[1]));
            }
            listOfPeople.add(person);
        }

        for (Person p : listOfPeople) {
            for (String s : listOfSkillNames) {
                PriorityQueue<Person> skillQueue = people.getOrDefault(s,
                        new PriorityQueue<>((a, b) -> b.getSkillLevel(s) - a.getSkillLevel(s)));
                skillQueue.add(p);
                people.put(s, skillQueue);
            }
        }

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
            Map<String, PriorityQueue<Integer>> skills = new HashMap<>();
            List<Pair<String, Integer>> skillsInOrder = new ArrayList<>();
            for (int j = 0; j < r; j++) {
                String[] skillAndLevel = sc.nextLine().split(" ");
                PriorityQueue<Integer> resources = skills.getOrDefault(skillAndLevel[0], new PriorityQueue<>((x, y) -> y - x));
                resources.add(Integer.parseInt(skillAndLevel[1]));
                skills.put(skillAndLevel[0], resources);
                skillsInOrder.add(Pair.of(skillAndLevel[0], Integer.parseInt(skillAndLevel[1])));
            }
            Project project = new Project(projectName, d, s, b, r, skills, skillsInOrder);
            projects.add(project);
        }
    }


}
