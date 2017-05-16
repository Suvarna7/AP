function [ table_name, data, json, ack ] = extract_json( json )
%EXTRACT_JSON Extractcts values from a json array
% JSON ARRAY FORMAT = [{},{},{},...{}]
json = strtrim(json);
table_name = 'unknown';
data = [];
ack =[];
time = '';

%First, check it is a json array
if (json(1)== '[' && json(end)==']')
    
    %Remove brackets
    json = json(2:end-1);
    %Get each element:
    starting = strfind(json,'{');
    ending = strfind(json, '}');
    if (length(starting)==length(ending))
        for row=1:length(starting)
            %Extract sample and remove column brackets
            sample = json(starting(row)+1:ending(row)-1);
            %Select each individual column
            columns = strfind(sample, ',');
            columns = [0;columns.'; length(sample)+1];
            line_cols =[];
            line_val =[];
            for col_index=1:size(columns)-1
                col_pair = strtrim(sample(columns(col_index)+1:columns(col_index+1)-1));
                column = strtrim(col_pair(1:strfind(col_pair,':')-1));
                value = strtrim(col_pair(strfind(col_pair, ':')+1:end));
                %Remove " at the begining adn the end
                column = column(2:end-1);
                value = value(2:end-1);
                if(strcmp(column,'table_name'))
                    table_name = value;
                elseif( strcmp(column, 'time_stamp'))
                    time = value;
                    line_val{1, end+1} = value;
                    line_cols{1, end+1} = column;
                elseif (~strcmp(column, 'synchronized'))
                    %Add values to the data[]
                    line_val{1, end+1} = value;
                    line_cols{1, end+1} = column;

                end
            end
            
            %Add new row
            if (length(data)<1)
                data =[data;line_cols];
            end
            if (length(line_val)==length(line_cols))
                data = [data;line_val];
            else
                line_val
            end
        

        end
        %Build ACK to send back
        %[{synchronized:y,table_name:empatica_table,time_stamp: "2016-10-13 14:22:06.218",usb_sync: y }]
        ack= strcat('[{synchronized:y, sensor_table:',table_name,', time_stamp: "',time,'", usb_sync: y}]');
    else
        'EXTRACT INVALID JSON: Unclosed JSONS inside the array'
    end

else
    'Format error in JSON'
end

end

