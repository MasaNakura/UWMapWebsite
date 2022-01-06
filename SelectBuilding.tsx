import React, {Component} from 'react';
import {Edge} from "./App";

interface SelectBuildingState {
    start: string; // selected short name of start building
    end: string // selected short name of end building
}

interface SelectBuildingProps {
    buildings: string[]; // list of all display of buildings with combined short and long name
    onChange(edges:Edge[], cost:number): void; // called when cleared
    onSelect(coordinate:[number, number], start:boolean): void; // called when selecting a building
}

class SelectBuilding extends Component<SelectBuildingProps, SelectBuildingState> {
    constructor(props: any) {
        super(props);
        this.state = {
            start: "Select",
            end: "Select",
        };
    }


    handleChange = (event: any, start: boolean) => {
        if (start) {
            this.setState({
                start: event.target.value,
            })

        } else {
            this.setState({
                end: event.target.value,
            })
        }
        // Locate dot on map only if a valid building is selected
        if (event.target.value !== "Select") {
            this.requestLocation(event.target.value, start);
        } else {
            this.props.onSelect([-1, -1], start);
        }
        // clears path whenever a new building is pressed
        this.props.onChange([], 0);
    }

    handleClear = () => {
        this.setState({
            start: "Select",
            end: "Select",
        })
        this.props.onChange([], 0);
        this.props.onSelect([-1, -1], true);
        this.props.onSelect([-1, -1], false);
    }

    requestLocation = async (building:string, start:boolean) => {
        try {
            let response = await fetch("https://us-central1-masa-uw-map.cloudfunctions.net/building?name=" +
                encodeURIComponent(building));
            if (!response.ok) {
                alert("Failed to get building from server! Expected: 200, Was: " + response.status);
            }

            // parsed object is an object with data about the requested shortName
            let parsedObject = await response.json();
            if (parsedObject === null) {
                console.log("no building found");
            } else {
                let x = parsedObject['x']; // x coordinate
                let y = parsedObject['y']; // y coordinate
                this.props.onSelect([x, y], start);
            }
        } catch (e) {
            alert("There was an error contacting the server");
        }
    }

    requestPath = async () => {
        if (this.state.start !== "Select" && this.state.end !== "Select") {
            try {
                let response = await fetch("https://us-central1-masa-uw-map.cloudfunctions.net/path?start=" +
                    encodeURIComponent(this.state.start) + "&end=" + encodeURIComponent(this.state.end));
                if (!response.ok) {
                    alert("Failed to get path from server! Expected: 200, Was: " + response.status);
                }

                // parsed object is the path from the start to end building
                let parsedObject = await response.json();

                if (parsedObject === null) {
                    console.log("no list found");
                } else {
                    let all: Edge[] = [];
                    let path = parsedObject['path']; // list of segments that build up path
                    for (let i = 0; i < path.length; i++) {
                        let segment = path[i];
                        let start = segment['start']; // start point
                        let end = segment['end']; // end point
                        let estimateCost = Math.round(parseFloat(segment['cost'])); // total cost
                        let edge: Edge = {
                            start: [parseFloat(start['x']), parseFloat(start['y'])],
                            end: [parseFloat(end['x']), parseFloat(end['y'])],
                            cost: estimateCost,
                        };
                        all.push(edge);
                    }
                    this.props.onChange(all, Math.round(parsedObject['cost']));
                }
            } catch (e) {
                alert("There was an error contacting the server");
                console.log(e);
            }
        } else {
            this.props.onChange([], 0);
        }
    }

    render() {
        let list: any[] = [];
        list.push(<option>Select</option>);
        // for all building list
        for (let i:number = 0; i < this.props.buildings.length; i++) {
            let separated:string[] = this.props.buildings[i].split(')');
            // substring to get only the short name
            let shortName:string = this.props.buildings[i].substring(0, 3);
            if (separated.length > 3) {
                shortName = separated[0] + ")";
            }
            // set the value attribute to short name, but display all
            list.push(
                <option value={shortName}>{this.props.buildings[i]}</option>
            )
        }

        return (
            <div id="select-building">
                Enter your starting location:
                <select placeholder="choose start!" value = {this.state.start}
                        onChange={evt => this.handleChange(evt, true)}>
                    {list}
                </select>
                Enter your destination:
                <select placeholder="choose start!" value = {this.state.end}
                        onChange={evt => this.handleChange(evt, false)}>
                    {list}
                </select>
                <button onClick={this.requestPath}>Draw Path!</button>
                <button onClick={this.handleClear}>Clear Path</button>
            </div>
        );
    }
}

export default SelectBuilding;