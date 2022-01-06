/*
 * Copyright (C) 2021 Kevin Zatloukal.  All rights reserved.  Permission is
 * hereby granted to students registered for University of Washington
 * CSE 331 for use solely during Spring Quarter 2021 for purposes of
 * the course.  No other use, copying, distribution, or modification
 * is permitted without prior written consent. Copyrights for
 * third-party components of this work must be honored.  Instructors
 * interested in reusing these course materials should contact the
 * author.
 */

import React, {Component} from 'react';
import MapView from "./MapView";
import SelectBuilding from "./SelectBuilding";
import "./App.css";

interface AppState {
    buildingDisplays: string[]; // list of all buildings in the campus
    totalCost: number; // total cost of the found path
    startBuilding: [number, number]; // the coordinates of a start location
    destBuilding: [number, number]; // the coordinates of a destination location
    edgeList: Edge[]; // the list of segments that make up a route
}

export interface Edge {
    start: [number, number]; // the coordinates of start of segment
    end: [number, number]; // the coordinates of destination of segment
    cost: number; // the length of segment
}

class App extends Component<{}, AppState> {

    constructor(props: any) {
        super(props);
        this.state = {
            buildingDisplays: [],
            totalCost: 0,
            startBuilding: [-1, -1], // -1 coordinates mean not set
            destBuilding: [-1, -1], // -1 coordinates mean not set
            edgeList: [],
        }
    }

    componentDidMount() {
        // get data of all buildings once at the start
        this.requestAllBuildings();
    }

    requestAllBuildings = async () => {
        try {
            let response = await fetch("https://us-central1-masa-uw-map.cloudfunctions.net/buildings");
            if (!response.ok) {
                alert("Failed to get list of buildings from server! Expected: 200, Was: " + response.status);
            }

            // the Java object is a map of shortnames to long names of all buildings
            let parsedObject = await response.json();

            if (parsedObject === null) {
                console.log("no list found");
            } else {
                let buildings: string[] = [];
                // for all buildings identify short and long name
                Object.entries(parsedObject).forEach(entry => {
                    let key:string = entry[0]; // short name
                    let value:any = entry[1]; // long name
                    buildings.push(key + " (" + value + ")");
                });
                this.setState({
                   buildingDisplays: buildings,
                });
            }
        } catch (e) {
            alert("There was an error contacting the server");
            console.log(e);
        }
    }

    onMark = (coordinate:[number, number], start:boolean) => {
        if (start) {
            this.setState({
                startBuilding: coordinate,
            });
        } else {
            this.setState({
                destBuilding: coordinate,
            });
        }
    }

    onDraw = (edges:Edge[], cost: number) => {
        this.setState({
            edgeList: edges,
            totalCost: cost
        });
    }

    render() {
        let list: any[] = [];
        list.push(<p>Approximate breakdown of total distance of the drawn route:</p>);
        let temp: Edge[] = this.state.edgeList;
        // for all segments, output the cost of each path along with its coordinates
        for (let i:number = 0; i < temp.length; i++) {
            let n = i + 1;
            list.push(<p>{n}. Walk {temp[i].cost} feet ({temp[i].start[0]},
                {temp[i].start[1]} to {temp[i].end[0]}, {temp[i].end[1]}) </p>);
        }
        return (
            <div>
                <p id="title">University of Washington Interactive Campus Map</p>
                <SelectBuilding buildings={this.state.buildingDisplays}
                                onChange={this.onDraw} onSelect={this.onMark}/>
                <p>Selected buildings will appear as dots on the map!
                    Start: Magenta, Destination: Blue</p>
                <p>Estimated Total Walking Distance: {this.state.totalCost} Feet! </p>
                <MapView edgeList={this.state.edgeList} start={this.state.startBuilding}
                         dest={this.state.destBuilding}/>
                {list}
            </div>
        );
    }

}

export default App;
